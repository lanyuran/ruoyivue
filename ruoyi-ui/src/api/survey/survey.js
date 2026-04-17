import request from '@/utils/request'

export function listSurvey(query) {
  return request({
    url: '/survey/survey/list',
    method: 'get',
    params: query
  })
}

export function getSurvey(surveyId) {
  return request({
    url: '/survey/survey/' + surveyId,
    method: 'get'
  })
}

export function addSurvey(data) {
  return request({
    url: '/survey/survey',
    method: 'post',
    data
  })
}

export function updateSurvey(data) {
  return request({
    url: '/survey/survey',
    method: 'put',
    data
  })
}

export function delSurvey(surveyId) {
  return request({
    url: '/survey/survey/' + surveyId,
    method: 'delete'
  })
}

export function exportSurvey(query) {
  return request({
    url: '/survey/survey/export',
    method: 'post',
    params: query
  })
}

export function getSurveyFill(surveyId) {
  return request({
    url: '/survey/fill/' + surveyId,
    method: 'get'
  })
}

export function submitSurveyFill(surveyId, data) {
  return request({
    url: '/survey/fill/' + surveyId + '/submit',
    method: 'post',
    data
  })
}

export function listSurveyAnswers(surveyId, query) {
  return request({
    url: '/survey/survey/' + surveyId + '/answers',
    method: 'get',
    params: query
  })
}

export function exportSurveyAnswers(surveyId, query) {
  return request({
    url: '/survey/survey/' + surveyId + '/answers/export',
    method: 'post',
    params: query
  })
}