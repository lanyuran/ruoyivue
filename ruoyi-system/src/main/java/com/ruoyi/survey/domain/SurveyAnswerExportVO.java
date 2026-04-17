package com.ruoyi.survey.domain;

import java.util.Date;
import com.ruoyi.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SurveyAnswerExportVO
{
    @Excel(name = "问卷ID")
    private Long surveyId;

    @Excel(name = "答卷ID")
    private Long answerId;

    @Excel(name = "参与者")
    private String participantKey;

    @Excel(name = "问题编码")
    private String questionKey;

    @Excel(name = "问题标题")
    private String questionLabel;

    @Excel(name = "问题类型")
    private String questionType;

    @Excel(name = "答案")
    private String answerText;

    @Excel(name = "选项值")
    private String optionValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    public Long getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Long surveyId)
    {
        this.surveyId = surveyId;
    }

    public Long getAnswerId()
    {
        return answerId;
    }

    public void setAnswerId(Long answerId)
    {
        this.answerId = answerId;
    }

    public String getParticipantKey()
    {
        return participantKey;
    }

    public void setParticipantKey(String participantKey)
    {
        this.participantKey = participantKey;
    }

    public String getQuestionKey()
    {
        return questionKey;
    }

    public void setQuestionKey(String questionKey)
    {
        this.questionKey = questionKey;
    }

    public String getQuestionLabel()
    {
        return questionLabel;
    }

    public void setQuestionLabel(String questionLabel)
    {
        this.questionLabel = questionLabel;
    }

    public String getQuestionType()
    {
        return questionType;
    }

    public void setQuestionType(String questionType)
    {
        this.questionType = questionType;
    }

    public String getAnswerText()
    {
        return answerText;
    }

    public void setAnswerText(String answerText)
    {
        this.answerText = answerText;
    }

    public String getOptionValue()
    {
        return optionValue;
    }

    public void setOptionValue(String optionValue)
    {
        this.optionValue = optionValue;
    }

    public Date getSubmitTime()
    {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime)
    {
        this.submitTime = submitTime;
    }
}

