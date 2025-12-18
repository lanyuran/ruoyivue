package com.ruoyi.survey.service;

import java.util.List;
import com.ruoyi.survey.domain.Survey;

/**
 * 问卷Service接口
 * 
 * @author ruoyi
 * @date 2024-05-20
 */
public interface ISurveyService 
{
    /**
     * 查询问卷
     * 
     * @param surveyId 问卷ID
     * @return 问卷
     */
    public Survey selectSurveyById(Long surveyId);

    /**
     * 查询问卷列表
     * 
     * @param survey 问卷
     * @return 问卷集合
     */
    public List<Survey> selectSurveyList(Survey survey);

    /**
     * 新增问卷
     * 
     * @param survey 问卷
     * @return 结果
     */
    public int insertSurvey(Survey survey);

    /**
     * 修改问卷
     * 
     * @param survey 问卷
     * @return 结果
     */
    public int updateSurvey(Survey survey);

    /**
     * 批量删除问卷
     * 
     * @param surveyIds 需要删除的问卷ID
     * @return 结果
     */
    public int deleteSurveyByIds(Long[] surveyIds);

    /**
     * 删除问卷信息
     * 
     * @param surveyId 问卷ID
     * @return 结果
     */
    public int deleteSurveyById(Long surveyId);
}
