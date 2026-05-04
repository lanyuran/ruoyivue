package com.ruoyi.patient.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.patient.domain.PatientChart;
import com.ruoyi.patient.domain.PatientProfile;
import com.ruoyi.patient.mapper.PatientVisitInfoMapper;
import com.ruoyi.patient.mapper.PatientChartMapper;
import com.ruoyi.patient.mapper.PatientProfileMapper;
import com.ruoyi.patient.domain.PatientVisitInfo;
import com.ruoyi.patient.service.IPatientVisitInfoService;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 鼻炎患者就诊信息主（包含文档中所有字段）Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-15
 */
@Service
public class PatientVisitInfoServiceImpl implements IPatientVisitInfoService
{
    private static final DateTimeFormatter AUTO_RECORD_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final String ROLE_KEY_PATIENT = "user";
    private static final String ROLE_KEY_DOCTOR = "doctor";
    private static final String ROLE_KEY_MANAGER = "manager";
    private static final String HOSPITAL_ROOT_NAME = "医院";

    private static final String OTHER_HOSPITAL_NAME = "其他医院";

    @Autowired
    private PatientVisitInfoMapper patientVisitInfoMapper;

    @Autowired
    private PatientProfileMapper patientProfileMapper;

    @Autowired
    private PatientChartMapper patientChartMapper;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysRoleMapper roleMapper;

    private final Map<Long, SysDept> deptCache = new ConcurrentHashMap<>();
    private final Map<Long, Long> hospitalDeptIdCache = new ConcurrentHashMap<>();
    private final Map<String, Long> hospitalNameDeptIdCache = new ConcurrentHashMap<>();
    private volatile List<TreeSelect> deptTreeCache;
    private volatile TreeSelect hospitalRootCache;

    /**
     * 查询鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param visitId 鼻炎患者就诊信息主（包含文档中所有字段）主键
     * @return 鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @Override
    public PatientVisitInfo selectPatientVisitInfoByVisitId(Long visitId)
    {
        PatientVisitInfo patientVisitInfo = patientVisitInfoMapper.selectPatientVisitInfoByVisitId(visitId);
        normalizeHospitalDisplay(patientVisitInfo);
        return patientVisitInfo;
    }

    /**
     * 查询鼻炎患者就诊信息主（包含文档中所有字段）列表
     *
     * @param patientVisitInfo 鼻炎患者就诊信息主（包含文档中所有字段）
     * @return 鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @Override
    public List<PatientVisitInfo> selectPatientVisitInfoList(PatientVisitInfo patientVisitInfo)
    {
        applyHospitalScope(patientVisitInfo);
        List<PatientVisitInfo> list = patientVisitInfoMapper.selectPatientVisitInfoList(patientVisitInfo);
        if (list != null)
        {
            for (PatientVisitInfo info : list)
            {
                normalizeHospitalDisplay(info);
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> listHospitalOptions()
    {
        LinkedHashMap<Long, Map<String, Object>> options = new LinkedHashMap<>();
        for (String hospitalName : getPresetHospitals())
        {
            Long deptId = ensureHospitalDeptId(hospitalName);
            if (deptId == null)
            {
                continue;
            }
            options.put(deptId, buildHospitalOption(deptId, hospitalName));
        }
        List<Map<String, Object>> rawOptions = patientVisitInfoMapper.selectDistinctHospitalOptions();
        if (rawOptions != null)
        {
            for (Map<String, Object> rawOption : rawOptions)
            {
                Long deptId = resolveHospitalOptionDeptId(rawOption);
                if (deptId == null || options.containsKey(deptId))
                {
                    continue;
                }
                SysDept dept = getDeptById(deptId);
                String deptName = dept == null ? null : trimToNull(dept.getDeptName());
                if (deptName == null)
                {
                    deptName = normalizeHospitalName(rawOption == null ? null : String.valueOf(rawOption.get("hospital")));
                }
                if (deptName == null || OTHER_HOSPITAL_NAME.equals(deptName))
                {
                    continue;
                }
                options.put(deptId, buildHospitalOption(deptId, deptName));
            }
        }
        if (!options.isEmpty())
        {
            return new ArrayList<>(options.values());
        }
        return buildHospitalOptionsFromDeptTree();
    }

    /**
     * 新增鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param patientVisitInfo 鼻炎患者就诊信息主（包含文档中所有字段）
     * @return 结果
     */
    @Override
    public int insertPatientVisitInfo(PatientVisitInfo patientVisitInfo)
    {
        applyDefaultFillTime(patientVisitInfo);
        applyHospitalInfoOnCreate(patientVisitInfo);
        syncPatientRelation(patientVisitInfo);
        validateVisitSubmission(patientVisitInfo);
        try
        {
            return patientVisitInfoMapper.insertPatientVisitInfo(patientVisitInfo);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("当前院区下该病历号在该就诊日期和填表时间已存在，请勿重复提交");
        }
    }

    /**
     * 修改鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param patientVisitInfo 鼻炎患者就诊信息主（包含文档中所有字段）
     * @return 结果
     */
    @Override
    public int updatePatientVisitInfo(PatientVisitInfo patientVisitInfo)
    {
        applyExistingFillTimeOnUpdate(patientVisitInfo);
        applyHospitalInfoOnCreate(patientVisitInfo);
        syncPatientRelation(patientVisitInfo);
        validateVisitSubmission(patientVisitInfo);
        try
        {
            return patientVisitInfoMapper.updatePatientVisitInfo(patientVisitInfo);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("当前院区下该病历号在该就诊日期和填表时间已存在，请勿重复提交");
        }
    }

    private void applyDefaultFillTime(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo != null && patientVisitInfo.getFillTime() == null)
        {
            patientVisitInfo.setFillTime(new Date());
        }
    }

    private void applyExistingFillTimeOnUpdate(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo == null || patientVisitInfo.getFillTime() != null || patientVisitInfo.getVisitId() == null)
        {
            return;
        }
        PatientVisitInfo existing = patientVisitInfoMapper.selectPatientVisitInfoByVisitId(patientVisitInfo.getVisitId());
        if (existing != null && existing.getFillTime() != null)
        {
            patientVisitInfo.setFillTime(existing.getFillTime());
            return;
        }
        applyDefaultFillTime(patientVisitInfo);
    }

    /**
     * 批量删除鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param visitIds 需要删除的鼻炎患者就诊信息主（包含文档中所有字段）主键
     * @return 结果
     */
    @Override
    public int deletePatientVisitInfoByVisitIds(Long[] visitIds)
    {
        return patientVisitInfoMapper.deletePatientVisitInfoByVisitIds(visitIds);
    }

    /**
     * 删除鼻炎患者就诊信息主（包含文档中所有字段）信息
     *
     * @param visitId 鼻炎患者就诊信息主（包含文档中所有字段）主键
     * @return 结果
     */
    @Override
    public int deletePatientVisitInfoByVisitId(Long visitId)
    {
        return patientVisitInfoMapper.deletePatientVisitInfoByVisitId(visitId);
    }

    private void applyHospitalScope(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo == null)
        {
            return;
        }
        LoginUser loginUser = getLoginUserOrNull();
        if (loginUser == null)
        {
            return;
        }
        SysUser user = loginUser.getUser();
        if (user == null || user.isAdmin())
        {
            return;
        }
        if (hasRole(user, ROLE_KEY_MANAGER))
        {
            return;
        }
        if (hasRole(user, ROLE_KEY_PATIENT))
        {
            applyPatientAccountScope(patientVisitInfo, user);
            return;
        }
        if (hasRole(user, ROLE_KEY_DOCTOR))
        {
            Long hospitalDeptId = resolveHospitalDeptId(user.getDeptId());
            if (hospitalDeptId != null)
            {
                patientVisitInfo.getParams().put("hospitalDeptId", hospitalDeptId);
                return;
            }
        }
        patientVisitInfo.setCreateBy(loginUser.getUsername());
    }

    private void applyPatientAccountScope(PatientVisitInfo patientVisitInfo, SysUser user)
    {
        patientVisitInfo.getParams().put("patientUserId", user.getUserId());
        patientVisitInfo.setCreateBy(null);
    }

    private void validateVisitSubmission(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo == null)
        {
            throw new ServiceException("患者信息不能为空");
        }
        String medicalRecordNo = StringUtils.trim(patientVisitInfo.getMedicalRecordNo());
        patientVisitInfo.setMedicalRecordNo(medicalRecordNo);
        if (StringUtils.isEmpty(medicalRecordNo))
        {
            throw new ServiceException("病例号不能为空");
        }
        if (patientVisitInfo.getHospitalDeptId() == null)
        {
            throw new ServiceException("就诊医院不能为空");
        }
        if (patientVisitInfo.getVisitTime() == null)
        {
            throw new ServiceException("就诊时间不能为空");
        }
        if (patientVisitInfo.getFillTime() == null)
        {
            throw new ServiceException("填表时间不能为空");
        }
        int duplicateCount = patientVisitInfoMapper.countVisitDuplicate(patientVisitInfo);
        if (duplicateCount > 0)
        {
            throw new ServiceException("当前院区下该病历号在该就诊日期和填表时间已存在，请勿重复提交");
        }
    }

    private void syncPatientRelation(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo == null)
        {
            throw new ServiceException("患者信息不能为空");
        }
        normalizeVisitBaseInfo(patientVisitInfo);
        Long patientUserId = resolvePatientAccountUserId(patientVisitInfo.getPhone());
        PatientChart existingChart = resolveExistingChart(patientVisitInfo);
        PatientProfile patientProfile = resolvePatientProfile(patientVisitInfo, patientUserId, existingChart);
        mergePatientProfile(patientProfile, patientVisitInfo, patientUserId);
        patientVisitInfo.setPatientId(patientProfile.getPatientId());
        PatientChart patientChart = resolvePatientChart(patientVisitInfo, patientProfile, existingChart);
        patientVisitInfo.setChartId(patientChart.getChartId());
        patientVisitInfo.setMedicalRecordNo(patientChart.getMedicalRecordNo());
        applyVisitSnapshot(patientVisitInfo, patientProfile, patientChart);
    }

    private void normalizeVisitBaseInfo(PatientVisitInfo patientVisitInfo)
    {
        patientVisitInfo.setName(trimToNull(patientVisitInfo.getName()));
        patientVisitInfo.setGender(trimToNull(patientVisitInfo.getGender()));
        patientVisitInfo.setParentName(trimToNull(patientVisitInfo.getParentName()));
        patientVisitInfo.setPhone(trimToNull(patientVisitInfo.getPhone()));
        patientVisitInfo.setHospital(trimToNull(patientVisitInfo.getHospital()));
        patientVisitInfo.setMedicalRecordNo(trimToNull(patientVisitInfo.getMedicalRecordNo()));
    }

    private SysDept getDeptById(Long deptId)
    {
        if (deptId == null)
        {
            return null;
        }
        SysDept cached = deptCache.get(deptId);
        if (cached != null)
        {
            return cached;
        }
        SysDept dept = deptService.selectDeptById(deptId);
        if (dept != null)
        {
            deptCache.put(deptId, dept);
        }
        return dept;
    }

    private List<TreeSelect> getDeptTree()
    {
        List<TreeSelect> cached = deptTreeCache;
        if (cached != null)
        {
            return cached;
        }
        synchronized (this)
        {
            if (deptTreeCache == null)
            {
                SysDept dept = new SysDept();
                deptTreeCache = deptService.selectDeptTreeListPublic(dept);
            }
            return deptTreeCache;
        }
    }

    private void clearHospitalCaches()
    {
        deptTreeCache = null;
        hospitalRootCache = null;
        hospitalNameDeptIdCache.clear();
        hospitalDeptIdCache.clear();
    }

    private Long resolvePatientAccountUserId(String phone)
    {
        String normalizedPhone = trimToNull(phone);
        if (normalizedPhone == null)
        {
            return null;
        }
        SysUser sysUser = userService.selectUserByUserName(normalizedPhone);
        if (sysUser == null)
        {
            return null;
        }
        List<SysRole> roles = roleMapper.selectRolesByUserName(normalizedPhone);
        if (!hasRole(roles, ROLE_KEY_PATIENT) || hasNonPatientRole(roles))
        {
            return null;
        }
        return sysUser.getUserId();
    }

    private PatientChart resolveExistingChart(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo == null || patientVisitInfo.getHospitalDeptId() == null
            || StringUtils.isEmpty(patientVisitInfo.getMedicalRecordNo()))
        {
            return null;
        }
        PatientChart query = new PatientChart();
        query.setHospitalDeptId(patientVisitInfo.getHospitalDeptId());
        query.setMedicalRecordNo(patientVisitInfo.getMedicalRecordNo());
        return patientChartMapper.selectPatientChartByHospitalRecord(query);
    }

    private PatientProfile resolvePatientProfile(PatientVisitInfo patientVisitInfo, Long patientUserId, PatientChart existingChart)
    {
        PatientProfile patientProfile = null;
        if (existingChart != null && existingChart.getPatientId() != null)
        {
            patientProfile = patientProfileMapper.selectPatientProfileByPatientId(existingChart.getPatientId());
        }
        if (patientProfile == null && patientVisitInfo.getPatientId() != null)
        {
            patientProfile = patientProfileMapper.selectPatientProfileByPatientId(patientVisitInfo.getPatientId());
        }
        if (patientProfile == null && patientUserId != null)
        {
            patientProfile = patientProfileMapper.selectPatientProfileByUserId(patientUserId);
        }
        if (patientProfile == null && canMatchProfileByIdentity(patientVisitInfo))
        {
            PatientProfile query = new PatientProfile();
            query.setName(patientVisitInfo.getName());
            query.setBirthDate(patientVisitInfo.getBirthDate());
            query.setParentName(patientVisitInfo.getParentName());
            query.setPhone(patientVisitInfo.getPhone());
            patientProfile = patientProfileMapper.selectPatientProfileByIdentity(query);
        }
        if (patientProfile != null)
        {
            return patientProfile;
        }

        PatientProfile newProfile = new PatientProfile();
        fillProfileFromVisit(newProfile, patientVisitInfo, patientUserId);
        String operator = resolveOperator(patientVisitInfo);
        Date now = new Date();
        newProfile.setCreateBy(operator);
        newProfile.setCreateTime(now);
        newProfile.setUpdateBy(operator);
        newProfile.setUpdateTime(now);
        newProfile.setRemark("由就诊记录自动维护患者主档");
        patientProfileMapper.insertPatientProfile(newProfile);
        return newProfile;
    }

    private void mergePatientProfile(PatientProfile patientProfile, PatientVisitInfo patientVisitInfo, Long patientUserId)
    {
        if (patientProfile == null)
        {
            throw new ServiceException("患者主档创建失败");
        }
        boolean changed = false;
        if (patientProfile.getUserId() == null && patientUserId != null)
        {
            patientProfile.setUserId(patientUserId);
            changed = true;
        }
        if (StringUtils.isNotEmpty(patientVisitInfo.getName()) && !StringUtils.equals(patientVisitInfo.getName(), patientProfile.getName()))
        {
            patientProfile.setName(patientVisitInfo.getName());
            changed = true;
        }
        if (StringUtils.isNotEmpty(patientVisitInfo.getGender()) && !StringUtils.equals(patientVisitInfo.getGender(), patientProfile.getGender()))
        {
            patientProfile.setGender(patientVisitInfo.getGender());
            changed = true;
        }
        if (patientVisitInfo.getBirthDate() != null && !patientVisitInfo.getBirthDate().equals(patientProfile.getBirthDate()))
        {
            patientProfile.setBirthDate(patientVisitInfo.getBirthDate());
            changed = true;
        }
        if (patientVisitInfo.getParentName() != null && !StringUtils.equals(patientVisitInfo.getParentName(), patientProfile.getParentName()))
        {
            patientProfile.setParentName(patientVisitInfo.getParentName());
            changed = true;
        }
        if (StringUtils.isNotEmpty(patientVisitInfo.getPhone()) && !StringUtils.equals(patientVisitInfo.getPhone(), patientProfile.getPhone()))
        {
            patientProfile.setPhone(patientVisitInfo.getPhone());
            changed = true;
        }
        if (changed)
        {
            patientProfile.setUpdateBy(resolveOperator(patientVisitInfo));
            patientProfile.setUpdateTime(new Date());
            patientProfile.setRemark("由就诊记录自动同步患者主档");
            patientProfileMapper.updatePatientProfile(patientProfile);
        }
    }

    private PatientChart resolvePatientChart(PatientVisitInfo patientVisitInfo, PatientProfile patientProfile, PatientChart existingChart)
    {
        if (patientProfile == null || patientProfile.getPatientId() == null)
        {
            throw new ServiceException("患者主档不存在");
        }
        PatientChart patientChart = existingChart;
        if (patientChart == null && patientVisitInfo.getChartId() != null)
        {
            patientChart = patientChartMapper.selectPatientChartByChartId(patientVisitInfo.getChartId());
        }
        if (patientChart == null && patientVisitInfo.getHospitalDeptId() != null
            && StringUtils.isEmpty(patientVisitInfo.getMedicalRecordNo()))
        {
            PatientChart patientHospitalQuery = new PatientChart();
            patientHospitalQuery.setPatientId(patientProfile.getPatientId());
            patientHospitalQuery.setHospitalDeptId(patientVisitInfo.getHospitalDeptId());
            patientChart = patientChartMapper.selectPatientChartByPatientHospital(patientHospitalQuery);
        }
        if (patientChart != null)
        {
            if (!patientProfile.getPatientId().equals(patientChart.getPatientId()))
            {
                throw new ServiceException("该医院病历号已关联其他患者，请核对后再提交");
            }
            boolean changed = false;
            if (StringUtils.isEmpty(patientChart.getMedicalRecordNo()) && StringUtils.isNotEmpty(patientVisitInfo.getMedicalRecordNo()))
            {
                patientChart.setMedicalRecordNo(patientVisitInfo.getMedicalRecordNo());
                changed = true;
            }
            if (StringUtils.isNotEmpty(patientVisitInfo.getHospital()) && !StringUtils.equals(patientVisitInfo.getHospital(), patientChart.getHospital()))
            {
                patientChart.setHospital(patientVisitInfo.getHospital());
                changed = true;
            }
            if (changed)
            {
                patientChart.setUpdateBy(resolveOperator(patientVisitInfo));
                patientChart.setUpdateTime(new Date());
                patientChart.setRemark("由就诊记录自动同步患者病历映射");
                patientChartMapper.updatePatientChart(patientChart);
            }
            if (StringUtils.isEmpty(patientChart.getMedicalRecordNo()))
            {
                patientChart.setMedicalRecordNo(generateMedicalRecordNo(patientVisitInfo.getPhone()));
                patientChart.setUpdateBy(resolveOperator(patientVisitInfo));
                patientChart.setUpdateTime(new Date());
                patientChartMapper.updatePatientChart(patientChart);
            }
            return patientChart;
        }

        PatientChart newChart = new PatientChart();
        newChart.setPatientId(patientProfile.getPatientId());
        newChart.setHospitalDeptId(patientVisitInfo.getHospitalDeptId());
        newChart.setHospital(patientVisitInfo.getHospital());
        newChart.setMedicalRecordNo(StringUtils.isNotEmpty(patientVisitInfo.getMedicalRecordNo())
            ? patientVisitInfo.getMedicalRecordNo() : generateMedicalRecordNo(patientVisitInfo.getPhone()));
        String operator = resolveOperator(patientVisitInfo);
        Date now = new Date();
        newChart.setCreateBy(operator);
        newChart.setCreateTime(now);
        newChart.setUpdateBy(operator);
        newChart.setUpdateTime(now);
        newChart.setRemark("由就诊记录自动维护患者病历映射");
        try
        {
            patientChartMapper.insertPatientChart(newChart);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("该医院病历号已被其他患者使用，请核对后再提交");
        }
        return newChart;
    }

    private void applyVisitSnapshot(PatientVisitInfo patientVisitInfo, PatientProfile patientProfile, PatientChart patientChart)
    {
        if (StringUtils.isEmpty(patientVisitInfo.getName()))
        {
            patientVisitInfo.setName(patientProfile.getName());
        }
        if (StringUtils.isEmpty(patientVisitInfo.getGender()))
        {
            patientVisitInfo.setGender(patientProfile.getGender());
        }
        if (patientVisitInfo.getBirthDate() == null)
        {
            patientVisitInfo.setBirthDate(patientProfile.getBirthDate());
        }
        if (patientVisitInfo.getParentName() == null)
        {
            patientVisitInfo.setParentName(patientProfile.getParentName());
        }
        if (StringUtils.isEmpty(patientVisitInfo.getPhone()))
        {
            patientVisitInfo.setPhone(patientProfile.getPhone());
        }
        if (StringUtils.isEmpty(patientVisitInfo.getHospital()))
        {
            patientVisitInfo.setHospital(patientChart.getHospital());
        }
        if (StringUtils.isEmpty(patientVisitInfo.getMedicalRecordNo()))
        {
            patientVisitInfo.setMedicalRecordNo(patientChart.getMedicalRecordNo());
        }
    }

    private void fillProfileFromVisit(PatientProfile patientProfile, PatientVisitInfo patientVisitInfo, Long patientUserId)
    {
        patientProfile.setUserId(patientUserId);
        patientProfile.setName(patientVisitInfo.getName());
        patientProfile.setGender(patientVisitInfo.getGender());
        patientProfile.setBirthDate(patientVisitInfo.getBirthDate());
        patientProfile.setParentName(patientVisitInfo.getParentName());
        patientProfile.setPhone(patientVisitInfo.getPhone());
    }

    private boolean canMatchProfileByIdentity(PatientVisitInfo patientVisitInfo)
    {
        return patientVisitInfo != null
            && StringUtils.isNotEmpty(patientVisitInfo.getName())
            && patientVisitInfo.getBirthDate() != null
            && StringUtils.isNotEmpty(patientVisitInfo.getPhone());
    }

    private String generateMedicalRecordNo(String phone)
    {
        String normalizedPhone = trimToNull(phone);
        String suffix = normalizedPhone == null || normalizedPhone.length() < 4
            ? "0000" : normalizedPhone.substring(normalizedPhone.length() - 4);
        return "P" + AUTO_RECORD_NO_FORMATTER.format(LocalDateTime.now()) + suffix;
    }

    private String resolveOperator(PatientVisitInfo patientVisitInfo)
    {
        LoginUser loginUser = getLoginUserOrNull();
        if (loginUser != null && loginUser.getUser() != null && StringUtils.isNotEmpty(loginUser.getUsername()))
        {
            return loginUser.getUsername();
        }
        if (patientVisitInfo != null && StringUtils.isNotEmpty(patientVisitInfo.getUpdateBy()))
        {
            return patientVisitInfo.getUpdateBy();
        }
        if (patientVisitInfo != null && StringUtils.isNotEmpty(patientVisitInfo.getCreateBy()))
        {
            return patientVisitInfo.getCreateBy();
        }
        return "system";
    }

    private String trimToNull(String value)
    {
        String trimmed = StringUtils.trim(value);
        return StringUtils.isEmpty(trimmed) ? null : trimmed;
    }

    private boolean hasRole(List<SysRole> roles, String roleKey)
    {
        if (roles == null || StringUtils.isEmpty(roleKey))
        {
            return false;
        }
        for (SysRole role : roles)
        {
            if (role == null || StringUtils.isEmpty(role.getRoleKey()))
            {
                continue;
            }
            String[] keys = role.getRoleKey().split(",");
            for (String key : keys)
            {
                if (roleKey.equals(StringUtils.trim(key)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasNonPatientRole(List<SysRole> roles)
    {
        if (roles == null)
        {
            return false;
        }
        for (SysRole role : roles)
        {
            if (role == null || StringUtils.isEmpty(role.getRoleKey()))
            {
                continue;
            }
            String[] keys = role.getRoleKey().split(",");
            for (String key : keys)
            {
                String trimmedKey = StringUtils.trim(key);
                if (StringUtils.isNotEmpty(trimmedKey) && !ROLE_KEY_PATIENT.equals(trimmedKey))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void applyHospitalInfoOnCreate(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo == null)
        {
            return;
        }
        LoginUser loginUser = getLoginUserOrNull();
        SysUser user = loginUser == null ? null : loginUser.getUser();
        boolean isAdmin = user != null && user.isAdmin();
        boolean isManager = hasRole(user, ROLE_KEY_MANAGER);
        boolean isPublicSubmit = loginUser == null;
        String hospitalName = normalizeHospitalName(patientVisitInfo.getHospital());
        if (StringUtils.isEmpty(hospitalName) && patientVisitInfo.getHospitalDeptId() == null)
        {
            hospitalName = OTHER_HOSPITAL_NAME;
        }
        patientVisitInfo.setHospital(StringUtils.isEmpty(hospitalName) ? null : hospitalName);
        if (patientVisitInfo.getHospitalDeptId() != null)
        {
            SysDept selected = getDeptById(patientVisitInfo.getHospitalDeptId());
            if (selected == null)
            {
                String presetHospitalName = getPresetHospitalCodeMap().get(patientVisitInfo.getHospitalDeptId());
                if (StringUtils.isNotEmpty(presetHospitalName))
                {
                    Long presetDeptId = ensureHospitalDeptId(presetHospitalName);
                    if (presetDeptId != null)
                    {
                        patientVisitInfo.setHospitalDeptId(presetDeptId);
                        patientVisitInfo.setHospital(presetHospitalName);
                        hospitalName = presetHospitalName;
                        selected = getDeptById(presetDeptId);
                    }
                }
            }
            if (selected == null)
            {
                throw new ServiceException("选择的医院不存在");
            }
            patientVisitInfo.setHospitalDeptId(selected.getDeptId());
            patientVisitInfo.setHospital(normalizeHospitalName(selected.getDeptName()));
            hospitalName = patientVisitInfo.getHospital();
        }
        else if (StringUtils.isNotEmpty(hospitalName))
        {
            Long resolvedId = resolveHospitalDeptIdByName(hospitalName);
            if (resolvedId != null)
            {
                patientVisitInfo.setHospitalDeptId(resolvedId);
            }
            else if (OTHER_HOSPITAL_NAME.equals(hospitalName))
            {
                Long otherHospitalDeptId = ensureHospitalDeptId(OTHER_HOSPITAL_NAME);
                if (otherHospitalDeptId == null)
                {
                    throw new ServiceException("其他医院节点创建失败，请检查医院根节点配置");
                }
                patientVisitInfo.setHospitalDeptId(otherHospitalDeptId);
            }
            else if (isAdmin || isManager || isPublicSubmit)
            {
                Long createdId = createHospitalDept(hospitalName, resolveOperator(patientVisitInfo));
                if (createdId == null)
                {
                    throw new ServiceException("未找到“医院”根节点，请先在医院管理中创建");
                }
                patientVisitInfo.setHospitalDeptId(createdId);
            }
            else
            {
                throw new ServiceException("就诊医院不存在，需该医院主管审批后添加");
            }
        }
        Long userHospitalDeptId = user == null ? null : resolveHospitalDeptId(user.getDeptId());
        if (user != null && !isAdmin && !isManager)
        {
            Long recordHospitalDeptId = patientVisitInfo.getHospitalDeptId();
            if (userHospitalDeptId != null && recordHospitalDeptId != null && !userHospitalDeptId.equals(recordHospitalDeptId))
            {
                throw new ServiceException("只能添加本医院数据，其他医院需该医院主管审批后添加");
            }
            if (recordHospitalDeptId == null && userHospitalDeptId != null)
            {
                patientVisitInfo.setHospitalDeptId(userHospitalDeptId);
            }
        }
        if (StringUtils.isEmpty(patientVisitInfo.getHospital()) && patientVisitInfo.getHospitalDeptId() != null)
        {
            SysDept hospitalDept = getDeptById(patientVisitInfo.getHospitalDeptId());
            if (hospitalDept != null && StringUtils.isNotEmpty(hospitalDept.getDeptName()))
            {
                patientVisitInfo.setHospital(hospitalDept.getDeptName());
            }
        }
        if (StringUtils.isEmpty(patientVisitInfo.getHospital()))
        {
            throw new ServiceException("就诊医院不能为空");
        }
    }

    private LoginUser getLoginUserOrNull()
    {
        try
        {
            return SecurityUtils.getLoginUser();
        }
        catch (ServiceException ex)
        {
            return null;
        }
    }

    private boolean hasRole(SysUser user, String roleKey)
    {
        if (user == null || StringUtils.isEmpty(roleKey) || user.getRoles() == null)
        {
            return false;
        }
        for (SysRole role : user.getRoles())
        {
            if (role == null || StringUtils.isEmpty(role.getRoleKey()))
            {
                continue;
            }
            String[] keys = role.getRoleKey().split(",");
            for (String key : keys)
            {
                if (roleKey.equals(StringUtils.trim(key)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private Long resolveHospitalDeptId(Long deptId)
    {
        if (deptId == null)
        {
            return null;
        }
        Long cachedDeptId = hospitalDeptIdCache.get(deptId);
        if (cachedDeptId != null)
        {
            return cachedDeptId;
        }
        SysDept dept = getDeptById(deptId);
        if (dept == null)
        {
            return deptId;
        }
        List<Long> ancestorIds = parseAncestorIds(dept.getAncestors());
        Long hospitalRootId = resolveHospitalRootId(ancestorIds, dept);
        if (hospitalRootId != null)
        {
            if (hospitalRootId.equals(dept.getDeptId()))
            {
                hospitalDeptIdCache.put(deptId, dept.getDeptId());
                return dept.getDeptId();
            }
            if (dept.getParentId() != null && hospitalRootId.equals(dept.getParentId()))
            {
                hospitalDeptIdCache.put(deptId, dept.getDeptId());
                return dept.getDeptId();
            }
            int rootIndex = ancestorIds.indexOf(hospitalRootId);
            if (rootIndex >= 0 && rootIndex + 1 < ancestorIds.size())
            {
                Long resolved = ancestorIds.get(rootIndex + 1);
                hospitalDeptIdCache.put(deptId, resolved);
                return resolved;
            }
            hospitalDeptIdCache.put(deptId, dept.getDeptId());
            return dept.getDeptId();
        }
        if (!ancestorIds.isEmpty())
        {
            Long resolved = ancestorIds.get(0);
            hospitalDeptIdCache.put(deptId, resolved);
            return resolved;
        }
        hospitalDeptIdCache.put(deptId, deptId);
        return deptId;
    }

    private List<Long> parseAncestorIds(String ancestors)
    {
        List<Long> ids = new ArrayList<>();
        if (StringUtils.isEmpty(ancestors))
        {
            return ids;
        }
        String[] parts = ancestors.split(",");
        for (String part : parts)
        {
            if (StringUtils.isEmpty(part) || "0".equals(part))
            {
                continue;
            }
            try
            {
                ids.add(Long.valueOf(part));
            }
            catch (NumberFormatException ex)
            {
                return new ArrayList<>();
            }
        }
        return ids;
    }

    private Long resolveHospitalRootId(List<Long> ancestorIds, SysDept dept)
    {
        if (dept != null && HOSPITAL_ROOT_NAME.equals(dept.getDeptName()))
        {
            return dept.getDeptId();
        }
        if (ancestorIds == null || ancestorIds.isEmpty())
        {
            return null;
        }
        for (Long ancestorId : ancestorIds)
        {
            SysDept ancestor = getDeptById(ancestorId);
            if (ancestor != null && HOSPITAL_ROOT_NAME.equals(ancestor.getDeptName()))
            {
                return ancestor.getDeptId();
            }
        }
        return null;
    }

    private Long resolveHospitalDeptIdByName(String hospitalName)
    {
        String normalizedHospitalName = normalizeHospitalName(hospitalName);
        if (StringUtils.isEmpty(normalizedHospitalName))
        {
            return null;
        }
        String trimmed = normalizedHospitalName;
        Long cachedDeptId = hospitalNameDeptIdCache.get(trimmed);
        if (cachedDeptId != null)
        {
            return cachedDeptId;
        }
        Long numericDeptId = parseLong(trimmed);
        if (numericDeptId != null)
        {
            Long normalizedDeptId = resolveHospitalDeptId(numericDeptId);
            Long candidateDeptId = normalizedDeptId == null ? numericDeptId : normalizedDeptId;
            if (getDeptById(candidateDeptId) != null)
            {
                hospitalNameDeptIdCache.put(trimmed, candidateDeptId);
                return candidateDeptId;
            }
        }
        List<TreeSelect> deptTree = getDeptTree();
        TreeSelect hospitalRoot = findHospitalRootNode(deptTree);
        if (hospitalRoot == null)
        {
            Long found = findDeptIdByName(deptTree, trimmed);
            if (found != null)
            {
                hospitalNameDeptIdCache.put(trimmed, found);
            }
            return found;
        }
        if (HOSPITAL_ROOT_NAME.equals(trimmed))
        {
            return hospitalRoot.getId();
        }
        if (hospitalRoot.getChildren() == null)
        {
            return null;
        }
        for (TreeSelect child : hospitalRoot.getChildren())
        {
            if (child == null || StringUtils.isEmpty(child.getLabel()))
            {
                continue;
            }
            if (trimmed.equals(StringUtils.trim(child.getLabel())))
            {
                return child.getId();
            }
        }
        return null;
    }

    private Long findDeptIdByName(List<TreeSelect> nodes, String deptName)
    {
        if (nodes == null || StringUtils.isEmpty(deptName))
        {
            return null;
        }
        for (TreeSelect node : nodes)
        {
            if (node == null)
            {
                continue;
            }
            if (deptName.equals(StringUtils.trim(node.getLabel())))
            {
                return node.getId();
            }
            Long found = findDeptIdByName(node.getChildren(), deptName);
            if (found != null)
            {
                return found;
            }
        }
        return null;
    }

    private TreeSelect resolveHospitalRootNode()
    {
        TreeSelect cached = hospitalRootCache;
        if (cached != null)
        {
            return cached;
        }
        TreeSelect resolved = findHospitalRootNode(getDeptTree());
        hospitalRootCache = resolved;
        return resolved;
    }

    private TreeSelect findHospitalRootNode(List<TreeSelect> nodes)
    {
        if (nodes == null)
        {
            return null;
        }
        for (TreeSelect node : nodes)
        {
            if (node == null)
            {
                continue;
            }
            if (HOSPITAL_ROOT_NAME.equals(StringUtils.trim(node.getLabel())))
            {
                return node;
            }
            TreeSelect found = findHospitalRootNode(node.getChildren());
            if (found != null)
            {
                return found;
            }
        }
        return null;
    }

    private Long createHospitalDept(String hospitalName, String username)
    {
        TreeSelect hospitalRoot = resolveHospitalRootNode();
        if (hospitalRoot == null)
        {
            return null;
        }
        SysDept dept = new SysDept();
        dept.setParentId(hospitalRoot.getId());
        dept.setDeptName(StringUtils.trim(hospitalName));
        dept.setOrderNum(0);
        dept.setStatus("0");
        dept.setCreateBy(username);
        deptService.insertDept(dept);
        clearHospitalCaches();
        return resolveHospitalDeptIdByName(hospitalName);
    }

    private List<Map<String, Object>> buildHospitalOptionsFromDeptTree()
    {
        List<Map<String, Object>> options = new ArrayList<>();
        TreeSelect hospitalRoot = resolveHospitalRootNode();
        if (hospitalRoot == null || hospitalRoot.getChildren() == null)
        {
            return options;
        }
        for (TreeSelect child : hospitalRoot.getChildren())
        {
            if (child == null || child.getId() == null || StringUtils.isEmpty(child.getLabel()))
            {
                continue;
            }
            options.add(buildHospitalOption(child.getId(), normalizeHospitalName(child.getLabel())));
        }
        return options;
    }

    private Long resolveHospitalOptionDeptId(Map<String, Object> rawOption)
    {
        if (rawOption == null || rawOption.isEmpty())
        {
            return null;
        }
        Long hospitalDeptId = parseLong(rawOption.get("hospitalDeptId"));
        if (hospitalDeptId != null)
        {
            Long normalizedDeptId = resolveHospitalDeptId(hospitalDeptId);
            if (normalizedDeptId != null && getDeptById(normalizedDeptId) != null)
            {
                return normalizedDeptId;
            }
            if (getDeptById(hospitalDeptId) != null)
            {
                return hospitalDeptId;
            }
        }
        String hospital = trimToNull(rawOption.get("hospital") == null ? null : String.valueOf(rawOption.get("hospital")));
        if (hospital == null)
        {
            return null;
        }
        String normalizedHospitalName = normalizeHospitalName(hospital);
        if (normalizedHospitalName == null)
        {
            return null;
        }
        return ensureHospitalDeptId(normalizedHospitalName);
    }

    private Map<String, Object> buildHospitalOption(Long deptId, String deptName)
    {
        Map<String, Object> option = new HashMap<>();
        option.put("deptId", deptId);
        option.put("deptName", deptName);
        return option;
    }

    private Long parseLong(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        String text = trimToNull(String.valueOf(value));
        if (text == null)
        {
            return null;
        }
        try
        {
            return new BigDecimal(text).longValueExact();
        }
        catch (ArithmeticException | NumberFormatException ex)
        {
            return null;
        }
    }

    private String normalizeHospitalName(String hospitalName)
    {
        String trimmed = trimToNull(hospitalName);
        if (trimmed == null)
        {
            return null;
        }
        Long presetCode = parseLong(trimmed);
        if (presetCode != null)
        {
            String presetHospitalName = getPresetHospitalCodeMap().get(presetCode);
            if (StringUtils.isNotEmpty(presetHospitalName))
            {
                return presetHospitalName;
            }
        }
        for (String presetHospital : getPresetHospitals())
        {
            if (presetHospital.equals(trimmed))
            {
                return presetHospital;
            }
        }
        return trimmed;
    }

    private Long ensureHospitalDeptId(String hospitalName)
    {
        String normalizedHospitalName = normalizeHospitalName(hospitalName);
        if (normalizedHospitalName == null)
        {
            return null;
        }
        Long resolvedId = resolveHospitalDeptIdByNameOnly(normalizedHospitalName);
        if (resolvedId != null)
        {
            return resolvedId;
        }
        return createHospitalDept(normalizedHospitalName, "system");
    }

    private void normalizeHospitalDisplay(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo == null)
        {
            return;
        }
        String normalizedHospitalName = normalizeHospitalName(patientVisitInfo.getHospital());
        if (StringUtils.isNotEmpty(normalizedHospitalName))
        {
            patientVisitInfo.setHospital(normalizedHospitalName);
        }
        if (OTHER_HOSPITAL_NAME.equals(patientVisitInfo.getHospital()))
        {
            return;
        }
        if (patientVisitInfo.getHospitalDeptId() != null)
        {
            SysDept dept = getDeptById(patientVisitInfo.getHospitalDeptId());
            if (dept != null && StringUtils.isNotEmpty(dept.getDeptName()))
            {
                patientVisitInfo.setHospital(normalizeHospitalName(dept.getDeptName()));
            }
        }
    }

    private Long resolveHospitalDeptIdByNameOnly(String hospitalName)
    {
        if (StringUtils.isEmpty(hospitalName))
        {
            return null;
        }
        String trimmed = StringUtils.trim(hospitalName);
        Long cachedDeptId = hospitalNameDeptIdCache.get(trimmed);
        if (cachedDeptId != null)
        {
            return cachedDeptId;
        }
        List<TreeSelect> deptTree = getDeptTree();
        TreeSelect hospitalRoot = findHospitalRootNode(deptTree);
        if (hospitalRoot == null)
        {
            Long found = findDeptIdByName(deptTree, trimmed);
            if (found != null)
            {
                hospitalNameDeptIdCache.put(trimmed, found);
            }
            return found;
        }
        if (HOSPITAL_ROOT_NAME.equals(trimmed))
        {
            return hospitalRoot.getId();
        }
        if (hospitalRoot.getChildren() == null)
        {
            return null;
        }
        for (TreeSelect child : hospitalRoot.getChildren())
        {
            if (child == null || StringUtils.isEmpty(child.getLabel()))
            {
                continue;
            }
            if (trimmed.equals(StringUtils.trim(child.getLabel())))
            {
                hospitalNameDeptIdCache.put(trimmed, child.getId());
                return child.getId();
            }
        }
        return null;
    }

    private List<String> getPresetHospitals()
    {
        return Arrays.asList(
            "浙江省中医院",
            "浙江省新华医院",
            "浙江省中山医院",
            "杭州市红十字会医院",
            "绍兴市中医院",
            "嘉兴市中医医院",
            "海宁市中医院",
            "宁波市镇海区中医医院",
            "嘉善县中医院",
            "宁海县中医院",
            "东阳市妇幼保健院",
            "湖州市南浔区中医院",
            "安吉县中医院",
            "长兴县中医院",
            "余姚市第二人民医院",
            "杭州市儿童医院",
            "社区卫生服务中心"
        );
    }

    private Map<Long, String> getPresetHospitalCodeMap()
    {
        Map<Long, String> codeMap = new LinkedHashMap<>();
        List<String> hospitals = getPresetHospitals();
        for (int i = 0; i < hospitals.size(); i++)
        {
            codeMap.put(Long.valueOf(i + 1L), hospitals.get(i));
        }
        codeMap.put(18L, "其他医院");
        return codeMap;
    }
}
