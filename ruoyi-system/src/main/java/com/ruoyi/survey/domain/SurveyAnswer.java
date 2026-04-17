package com.ruoyi.survey.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class SurveyAnswer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long answerId;

    private Long surveyId;

    @Excel(name = "问卷标题")
    private String surveyTitle;

    @Excel(name = "参与者标识")
    private String participantKey;

    @Excel(name = "提交IP")
    private String submitIp;

    private String userAgent;

    private String answerJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    public Long getAnswerId()
    {
        return answerId;
    }

    public void setAnswerId(Long answerId)
    {
        this.answerId = answerId;
    }

    public Long getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Long surveyId)
    {
        this.surveyId = surveyId;
    }

    public String getSurveyTitle()
    {
        return surveyTitle;
    }

    public void setSurveyTitle(String surveyTitle)
    {
        this.surveyTitle = surveyTitle;
    }

    public String getParticipantKey()
    {
        return participantKey;
    }

    public void setParticipantKey(String participantKey)
    {
        this.participantKey = participantKey;
    }

    public String getSubmitIp()
    {
        return submitIp;
    }

    public void setSubmitIp(String submitIp)
    {
        this.submitIp = submitIp;
    }

    public String getUserAgent()
    {
        return userAgent;
    }

    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    public String getAnswerJson()
    {
        return answerJson;
    }

    public void setAnswerJson(String answerJson)
    {
        this.answerJson = answerJson;
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

