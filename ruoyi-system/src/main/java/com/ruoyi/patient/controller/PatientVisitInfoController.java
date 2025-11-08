package com.ruoyi.patient.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.patient.domain.PatientVisitInfo;
import com.ruoyi.patient.domain.PatientVisitExportVO;
import com.ruoyi.patient.service.IPatientVisitInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 鼻炎患者就诊信息主（包含文档中所有字段）Controller
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
@RestController
@RequestMapping("/patient/information")
public class PatientVisitInfoController extends BaseController
{
    @Autowired
    private IPatientVisitInfoService patientVisitInfoService;

    /**
     * 查询鼻炎患者就诊信息主（包含文档中所有字段）列表
     */
    @PreAuthorize("@ss.hasPermi('patient:information:list')")
    @GetMapping("/list")
    public TableDataInfo list(PatientVisitInfo patientVisitInfo)
    {
        startPage();
        List<PatientVisitInfo> list = patientVisitInfoService.selectPatientVisitInfoList(patientVisitInfo);
        return getDataTable(list);
    }

    /**
     * 导出鼻炎患者就诊信息主（包含文档中所有字段）列表
     */
    @PreAuthorize("@ss.hasPermi('patient:information:export')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PatientVisitInfo patientVisitInfo) {
        // 查询数据库
        List<PatientVisitInfo> list = patientVisitInfoService.selectPatientVisitInfoList(patientVisitInfo);

        // 转换为导出 VO
        List<PatientVisitExportVO> exportList = list.stream().map(p -> {
            PatientVisitExportVO vo = new PatientVisitExportVO();
            vo.setName(p.getName());
            vo.setGender(p.getGender());
            vo.setBirthDate(p.getBirthDate());
            vo.setVisitTime(p.getVisitTime());
            vo.setHospital(p.getHospital());
            vo.setMedicalRecordNo(p.getMedicalRecordNo());
            vo.setParentName(p.getParentName());
            vo.setPhone(p.getPhone());
            vo.setPastMedication(p.getPastMedication());
            vo.setChiefComplaint(p.getChiefComplaint());
            vo.setMainSymptom(p.getMainSymptom());
            vo.setComorbidity(p.getComorbidity());
            vo.setPhysicalExam(p.getPhysicalExam());
            vo.setTonguePulse(p.getTonguePulse());
            vo.setAllergenTotalIge(p.getAllergenTotalIge());
            vo.setAllergenSpecificIge(p.getAllergenSpecificIge());
            vo.setTcmDiagnosis(p.getTcmDiagnosis());
            vo.setTcmTreatment(p.getTcmTreatment());
            vo.setTcmExternalPrescription(p.getTcmExternalPrescription());
            return vo;
        }).collect(Collectors.toList());

        // 导出 Excel
        ExcelUtil<PatientVisitExportVO> util = new ExcelUtil<>(PatientVisitExportVO.class);
        util.exportExcel(response, exportList, "鼻炎患者就诊信息主数据");
    }



    /**
     * 获取鼻炎患者就诊信息主（包含文档中所有字段）详细信息
     */
    @PreAuthorize("@ss.hasPermi('patient:information:query')")
    @GetMapping(value = "/{visitId}")
    public AjaxResult getInfo(@PathVariable("visitId") Long visitId)
    {
        return success(patientVisitInfoService.selectPatientVisitInfoByVisitId(visitId));
    }

    /**
     * 新增鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @PreAuthorize("@ss.hasPermi('patient:information:add')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PatientVisitInfo patientVisitInfo)
    {
        return toAjax(patientVisitInfoService.insertPatientVisitInfo(patientVisitInfo));
    }

    /**
     * 修改鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @PreAuthorize("@ss.hasPermi('patient:information:edit')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PatientVisitInfo patientVisitInfo)
    {
        return toAjax(patientVisitInfoService.updatePatientVisitInfo(patientVisitInfo));
    }

    /**
     * 删除鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @PreAuthorize("@ss.hasPermi('patient:information:remove')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{visitIds}")
    public AjaxResult remove(@PathVariable Long[] visitIds)
    {
        return toAjax(patientVisitInfoService.deletePatientVisitInfoByVisitIds(visitIds));
    }
    /**
     * 获取鼻炎患者就诊信息主（包含文档中所有字段）详细信息
     */


}
