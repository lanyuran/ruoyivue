package com.ruoyi.patient.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.patient.mapper.PatientVisitInfoMapper;
import com.ruoyi.patient.domain.PatientVisitInfo;
import com.ruoyi.patient.service.IPatientVisitInfoService;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 鼻炎患者就诊信息主（包含文档中所有字段）Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-15
 */
@Service
public class PatientVisitInfoServiceImpl implements IPatientVisitInfoService
{
    private static final String ROLE_KEY_DEPT_DOCTOR = "dept_doctor";
    private static final String ROLE_KEY_DEPT_MANAGER = "dept_manager";
    private static final String ROLE_KEY_CAMPUS_MANAGER = "campus_manager";
    private static final String HOSPITAL_ROOT_NAME = "医院";

    @Autowired
    private PatientVisitInfoMapper patientVisitInfoMapper;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param visitId 鼻炎患者就诊信息主（包含文档中所有字段）主键
     * @return 鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @Override
    public PatientVisitInfo selectPatientVisitInfoByVisitId(Long visitId)
    {
        return patientVisitInfoMapper.selectPatientVisitInfoByVisitId(visitId);
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
        return patientVisitInfoMapper.selectPatientVisitInfoList(patientVisitInfo);
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
        applyHospitalInfoOnCreate(patientVisitInfo);
        return patientVisitInfoMapper.insertPatientVisitInfo(patientVisitInfo);
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
        applyHospitalInfoOnCreate(patientVisitInfo);
        return patientVisitInfoMapper.updatePatientVisitInfo(patientVisitInfo);
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
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return;
        }
        SysUser user = loginUser.getUser();
        if (user == null || user.isAdmin())
        {
            return;
        }
        if (hasRole(user, ROLE_KEY_CAMPUS_MANAGER))
        {
            return;
        }
        if (hasRole(user, ROLE_KEY_DEPT_DOCTOR) || hasRole(user, ROLE_KEY_DEPT_MANAGER))
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

    private void applyHospitalInfoOnCreate(PatientVisitInfo patientVisitInfo)
    {
        if (patientVisitInfo == null)
        {
            return;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return;
        }
        SysUser user = loginUser.getUser();
        if (user == null)
        {
            return;
        }
        boolean isAdmin = user.isAdmin();
        boolean isCampusManager = hasRole(user, ROLE_KEY_CAMPUS_MANAGER);
        String hospitalName = StringUtils.trim(patientVisitInfo.getHospital());
        patientVisitInfo.setHospital(StringUtils.isEmpty(hospitalName) ? null : hospitalName);
        if (patientVisitInfo.getHospitalDeptId() != null)
        {
            SysDept selected = deptService.selectDeptById(patientVisitInfo.getHospitalDeptId());
            if (selected == null)
            {
                throw new ServiceException("选择的医院不存在");
            }
            patientVisitInfo.setHospitalDeptId(selected.getDeptId());
            patientVisitInfo.setHospital(selected.getDeptName());
            hospitalName = selected.getDeptName();
        }
        else if (StringUtils.isNotEmpty(hospitalName))
        {
            Long resolvedId = resolveHospitalDeptIdByName(hospitalName);
            if (resolvedId != null)
            {
                patientVisitInfo.setHospitalDeptId(resolvedId);
            }
            else if (isAdmin || isCampusManager)
            {
                Long createdId = createHospitalDept(hospitalName, loginUser.getUsername());
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
        Long userHospitalDeptId = resolveHospitalDeptId(user.getDeptId());
        if (!isAdmin && !isCampusManager)
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
            SysDept hospitalDept = deptService.selectDeptById(patientVisitInfo.getHospitalDeptId());
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
        SysDept dept = deptService.selectDeptById(deptId);
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
                return dept.getDeptId();
            }
            if (dept.getParentId() != null && hospitalRootId.equals(dept.getParentId()))
            {
                return dept.getDeptId();
            }
            int rootIndex = ancestorIds.indexOf(hospitalRootId);
            if (rootIndex >= 0 && rootIndex + 1 < ancestorIds.size())
            {
                return ancestorIds.get(rootIndex + 1);
            }
            return dept.getDeptId();
        }
        if (!ancestorIds.isEmpty())
        {
            return ancestorIds.get(0);
        }
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
            SysDept ancestor = deptService.selectDeptById(ancestorId);
            if (ancestor != null && HOSPITAL_ROOT_NAME.equals(ancestor.getDeptName()))
            {
                return ancestor.getDeptId();
            }
        }
        return null;
    }

    private Long resolveHospitalDeptIdByName(String hospitalName)
    {
        if (StringUtils.isEmpty(hospitalName))
        {
            return null;
        }
        String trimmed = StringUtils.trim(hospitalName);
        SysDept dept = new SysDept();
        List<TreeSelect> deptTree = deptService.selectDeptTreeListPublic(dept);
        TreeSelect hospitalRoot = findHospitalRootNode(deptTree);
        if (hospitalRoot == null)
        {
            return findDeptIdByName(deptTree, trimmed);
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
        SysDept dept = new SysDept();
        List<TreeSelect> deptTree = deptService.selectDeptTreeListPublic(dept);
        return findHospitalRootNode(deptTree);
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
        return resolveHospitalDeptIdByName(hospitalName);
    }
}
