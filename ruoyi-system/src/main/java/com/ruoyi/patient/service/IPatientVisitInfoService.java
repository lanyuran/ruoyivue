package com.ruoyi.patient.service;

import java.util.List;
import com.ruoyi.patient.domain.PatientVisitInfo;

/**
 * 鼻炎患者就诊信息主（包含文档中所有字段）Service接口
 *
 * @author ruoyi
 * @date 2025-11-15
 */
public interface IPatientVisitInfoService
{
    /**
     * 查询鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param visitId 鼻炎患者就诊信息主（包含文档中所有字段）主键
     * @return 鼻炎患者就诊信息主（包含文档中所有字段）
     */
    public PatientVisitInfo selectPatientVisitInfoByVisitId(Long visitId);

    /**
     * 查询鼻炎患者就诊信息主（包含文档中所有字段）列表
     *
     * @param patientVisitInfo 鼻炎患者就诊信息主（包含文档中所有字段）
     * @return 鼻炎患者就诊信息主（包含文档中所有字段）集合
     */
    public List<PatientVisitInfo> selectPatientVisitInfoList(PatientVisitInfo patientVisitInfo);

    /**
     * 新增鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param patientVisitInfo 鼻炎患者就诊信息主（包含文档中所有字段）
     * @return 结果
     */
    public int insertPatientVisitInfo(PatientVisitInfo patientVisitInfo);

    /**
     * 修改鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param patientVisitInfo 鼻炎患者就诊信息主（包含文档中所有字段）
     * @return 结果
     */
    public int updatePatientVisitInfo(PatientVisitInfo patientVisitInfo);

    /**
     * 批量删除鼻炎患者就诊信息主（包含文档中所有字段）
     *
     * @param visitIds 需要删除的鼻炎患者就诊信息主（包含文档中所有字段）主键集合
     * @return 结果
     */
    public int deletePatientVisitInfoByVisitIds(Long[] visitIds);

    /**
     * 删除鼻炎患者就诊信息主（包含文档中所有字段）信息
     *
     * @param visitId 鼻炎患者就诊信息主（包含文档中所有字段）主键
     * @return 结果
     */
    public int deletePatientVisitInfoByVisitId(Long visitId);
}
