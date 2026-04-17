package com.ruoyi.survey.service;

import java.util.List;
import com.ruoyi.survey.domain.Survey;
import com.ruoyi.survey.domain.SurveyAnswer;
import com.ruoyi.survey.domain.SurveyAnswerExportVO;
import com.ruoyi.survey.domain.SurveySubmitItem;

public interface ISurveyAnswerService
{
    public Survey getEnabledSurvey(Long surveyId);

    public SurveyAnswer selectSurveyAnswerBySurveyAndParticipant(Long surveyId, String participantKey);

    public Long submitSurveyAnswer(Long surveyId, String participantKey, String submitIp, String userAgent, List<SurveySubmitItem> answers);

    public List<SurveyAnswer> selectSurveyAnswerList(SurveyAnswer surveyAnswer);

    public List<SurveyAnswerExportVO> selectSurveyAnswerExportList(SurveyAnswer surveyAnswer);
}

