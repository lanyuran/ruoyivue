package com.ruoyi.survey.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.survey.domain.Survey;
import com.ruoyi.survey.domain.SurveyAnswer;
import com.ruoyi.survey.domain.SurveySubmitRequest;
import com.ruoyi.survey.service.ISurveyAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/survey/fill")
public class SurveyFillController
{
    @Autowired
    private ISurveyAnswerService surveyAnswerService;

    @GetMapping("/{surveyId}")
    public AjaxResult getFillMeta(@PathVariable("surveyId") Long surveyId)
    {
        Survey survey = surveyAnswerService.getEnabledSurvey(surveyId);
        String participantKey = resolveParticipantKey(null);
        SurveyAnswer surveyAnswer = StringUtils.isEmpty(participantKey) ? null
            : surveyAnswerService.selectSurveyAnswerBySurveyAndParticipant(surveyId, participantKey);
        Map<String, Object> payload = new HashMap<>();
        payload.put("surveyId", survey.getSurveyId());
        payload.put("title", survey.getTitle());
        payload.put("content", survey.getContent());
        payload.put("submitted", surveyAnswer != null);
        payload.put("submitTime", surveyAnswer == null ? null : surveyAnswer.getSubmitTime());
        return AjaxResult.success(payload);
    }

    @PostMapping("/{surveyId}/submit")
    public AjaxResult submit(@PathVariable("surveyId") Long surveyId,
            @RequestBody(required = false) SurveySubmitRequest request,
            HttpServletRequest httpServletRequest)
    {
        SurveySubmitRequest submitRequest = request == null ? new SurveySubmitRequest() : request;
        String participantKey = resolveParticipantKey(submitRequest.getParticipantKey());
        if (StringUtils.isEmpty(participantKey))
        {
            return AjaxResult.error("请先登录后再提交问卷");
        }
        String submitIp = IpUtils.getIpAddr();
        String userAgent = httpServletRequest == null ? "" : StringUtils.trimToEmpty(httpServletRequest.getHeader("User-Agent"));
        Long answerId = surveyAnswerService.submitSurveyAnswer(surveyId, participantKey, submitIp, userAgent, submitRequest.getAnswers());
        Map<String, Object> payload = new HashMap<>();
        payload.put("answerId", answerId);
        return AjaxResult.success("提交成功", payload);
    }

    private String resolveParticipantKey(String requestParticipantKey)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null && loginUser.getUser() != null && StringUtils.isNotEmpty(loginUser.getUser().getUserName()))
        {
            return loginUser.getUser().getUserName();
        }
        return StringUtils.trimToEmpty(requestParticipantKey);
    }
}

