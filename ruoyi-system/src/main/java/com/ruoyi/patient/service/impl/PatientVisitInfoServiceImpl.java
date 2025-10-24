package com.ruoyi.patient.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.patient.mapper.PatientVisitInfoMapper;
import com.ruoyi.patient.domain.PatientVisitInfo;
import com.ruoyi.patient.service.IPatientVisitInfoService;

/**
 * 鼻炎患者就诊信息主（包含文档中所有字段）Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
@Service
public class PatientVisitInfoServiceImpl implements IPatientVisitInfoService 
{
    @Autowired
    private PatientVisitInfoMapper patientVisitInfoMapper;

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
}
