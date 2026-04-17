package com.ruoyi.survey.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.survey.domain.SurveyAnswer;
import com.ruoyi.survey.domain.SurveyAnswerItem;
import com.ruoyi.survey.domain.SurveyAnswerExportVO;

public interface SurveyAnswerMapper
{
    public SurveyAnswer selectSurveyAnswerBySurveyAndParticipant(@Param("surveyId") Long surveyId,
            @Param("participantKey") String participantKey);

    public int insertSurveyAnswer(SurveyAnswer surveyAnswer);

    public int batchInsertSurveyAnswerItem(List<SurveyAnswerItem> items);

    public List<SurveyAnswer> selectSurveyAnswerList(SurveyAnswer surveyAnswer);

    public List<SurveyAnswerExportVO> selectSurveyAnswerExportList(SurveyAnswer surveyAnswer);
}

