package com.ruoyi.survey.domain;

import java.math.BigDecimal;

public class SurveySubmitItem
{
    private String questionKey;

    private String questionLabel;

    private String questionType;

    private String answerValue;

    private String optionValue;

    private BigDecimal score;

    private Integer sortNo;

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

    public String getAnswerValue()
    {
        return answerValue;
    }

    public void setAnswerValue(String answerValue)
    {
        this.answerValue = answerValue;
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
}

