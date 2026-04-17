package com.ruoyi.survey.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.survey.domain.Survey;
import com.ruoyi.survey.domain.SurveyAnswer;
import com.ruoyi.survey.domain.SurveyAnswerExportVO;
import com.ruoyi.survey.domain.SurveyAnswerItem;
import com.ruoyi.survey.domain.SurveySubmitItem;
import com.ruoyi.survey.mapper.SurveyAnswerMapper;
import com.ruoyi.survey.mapper.SurveyMapper;
import com.ruoyi.survey.service.ISurveyAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SurveyAnswerServiceImpl implements ISurveyAnswerService
{
    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private SurveyAnswerMapper surveyAnswerMapper;

    @Override
    public Survey getEnabledSurvey(Long surveyId)
    {
        Survey survey = surveyMapper.selectSurveyById(surveyId);
        if (survey == null || !"0".equals(survey.getStatus()))
        {
            throw new ServiceException("问卷不存在或已停用");
        }
        return survey;
    }

    @Override
    public SurveyAnswer selectSurveyAnswerBySurveyAndParticipant(Long surveyId, String participantKey)
    {
        if (surveyId == null || StringUtils.isEmpty(participantKey))
        {
            return null;
        }
        return surveyAnswerMapper.selectSurveyAnswerBySurveyAndParticipant(surveyId, participantKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitSurveyAnswer(Long surveyId, String participantKey, String submitIp, String userAgent, List<SurveySubmitItem> answers)
    {
        getEnabledSurvey(surveyId);
        if (StringUtils.isEmpty(participantKey))
        {
            throw new ServiceException("参与者标识不能为空");
        }
        SurveyAnswer existed = selectSurveyAnswerBySurveyAndParticipant(surveyId, participantKey);
        if (existed != null)
        {
            throw new ServiceException("您已提交过此问卷，请勿重复提交");
        }
        SurveyAnswer answer = new SurveyAnswer();
        answer.setSurveyId(surveyId);
        answer.setParticipantKey(participantKey);
        answer.setSubmitIp(StringUtils.trimToEmpty(submitIp));
        answer.setUserAgent(StringUtils.trimToEmpty(userAgent));
        answer.setAnswerJson(JSON.toJSONString(answers == null ? new ArrayList<>() : answers));
        answer.setSubmitTime(DateUtils.getNowDate());
        answer.setCreateBy(participantKey);
        answer.setCreateTime(DateUtils.getNowDate());
        surveyAnswerMapper.insertSurveyAnswer(answer);

        if (answers != null && !answers.isEmpty())
        {
            List<SurveyAnswerItem> items = new ArrayList<>(answers.size());
            for (int i = 0; i < answers.size(); i++)
            {
                SurveySubmitItem submitItem = answers.get(i);
                if (submitItem == null)
                {
                    continue;
                }
                SurveyAnswerItem item = new SurveyAnswerItem();
                item.setAnswerId(answer.getAnswerId());
                item.setSurveyId(surveyId);
                item.setQuestionKey(submitItem.getQuestionKey());
                item.setQuestionLabel(submitItem.getQuestionLabel());
                item.setQuestionType(submitItem.getQuestionType());
                item.setAnswerText(submitItem.getAnswerValue());
                item.setOptionValue(submitItem.getOptionValue());
                item.setScore(submitItem.getScore());
                item.setSortNo(submitItem.getSortNo() == null ? i + 1 : submitItem.getSortNo());
                item.setSubmitTime(answer.getSubmitTime());
                items.add(item);
            }
            if (!items.isEmpty())
            {
                surveyAnswerMapper.batchInsertSurveyAnswerItem(items);
            }
        }
        return answer.getAnswerId();
    }

    @Override
    public List<SurveyAnswer> selectSurveyAnswerList(SurveyAnswer surveyAnswer)
    {
        return surveyAnswerMapper.selectSurveyAnswerList(surveyAnswer);
    }

    @Override
    public List<SurveyAnswerExportVO> selectSurveyAnswerExportList(SurveyAnswer surveyAnswer)
    {
        return surveyAnswerMapper.selectSurveyAnswerExportList(surveyAnswer);
    }
}

