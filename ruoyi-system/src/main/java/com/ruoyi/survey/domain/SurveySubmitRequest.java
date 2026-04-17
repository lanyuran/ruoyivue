package com.ruoyi.survey.domain;

import java.util.ArrayList;
import java.util.List;

public class SurveySubmitRequest
{
    private String participantKey;

    private List<SurveySubmitItem> answers = new ArrayList<>();

    public String getParticipantKey()
    {
        return participantKey;
    }

    public void setParticipantKey(String participantKey)
    {
        this.participantKey = participantKey;
    }

    public List<SurveySubmitItem> getAnswers()
    {
        return answers;
    }

    public void setAnswers(List<SurveySubmitItem> answers)
    {
        this.answers = answers;
    }
}

