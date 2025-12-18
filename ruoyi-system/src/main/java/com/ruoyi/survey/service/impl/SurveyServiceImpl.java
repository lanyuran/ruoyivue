package com.ruoyi.survey.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.survey.mapper.SurveyMapper;
import com.ruoyi.survey.domain.Survey;
import com.ruoyi.survey.service.ISurveyService;

/**
 * 问卷Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-05-20
 */
@Service
public class SurveyServiceImpl implements ISurveyService 
{
    @Autowired
    private SurveyMapper surveyMapper;

    /**
     * 查询问卷
     * 
     * @param surveyId 问卷ID
     * @return 问卷
     */
    @Override
    public Survey selectSurveyById(Long surveyId)
    {
        return surveyMapper.selectSurveyById(surveyId);
    }

    /**
     * 查询问卷列表
     * 
     * @param survey 问卷
     * @return 问卷
     */
    @Override
    public List<Survey> selectSurveyList(Survey survey)
    {
        return surveyMapper.selectSurveyList(survey);
    }

    /**
     * 新增问卷
     * 
     * @param survey 问卷
     * @return 结果
     */
    @Override
    public int insertSurvey(Survey survey)
    {
        survey.setCreateTime(DateUtils.getNowDate());
        return surveyMapper.insertSurvey(survey);
    }

    /**
     * 修改问卷
     * 
     * @param survey 问卷
     * @return 结果
     */
    @Override
    public int updateSurvey(Survey survey)
    {
        survey.setUpdateTime(DateUtils.getNowDate());
        return surveyMapper.updateSurvey(survey);
    }

    /**
     * 批量删除问卷
     * 
     * @param surveyIds 需要删除的问卷ID
     * @return 结果
     */
    @Override
    public int deleteSurveyByIds(Long[] surveyIds)
    {
        return surveyMapper.deleteSurveyByIds(surveyIds);
    }

    /**
     * 删除问卷信息
     * 
     * @param surveyId 问卷ID
     * @return 结果
     */
    @Override
    public int deleteSurveyById(Long surveyId)
    {
        return surveyMapper.deleteSurveyById(surveyId);
    }
}
