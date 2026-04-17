package com.ruoyi.survey.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SurveyAnswerItem
{
    private Long itemId;

    private Long answerId;

    private Long surveyId;

    private String questionKey;

    private String questionLabel;

    private String questionType;

    private String answerText;

    private String optionValue;

    private BigDecimal score;

    private Integer sortNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

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

    public BigDecimal getScore()
    {
        return score;
    }

    public void setScore(BigDecimal score)
    {
        this.score = score;
    }

    public Integer getSortNo()
    {
        return sortNo;
    }

    public void setSortNo(Integer sortNo)
    {
        this.sortNo = sortNo;
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

