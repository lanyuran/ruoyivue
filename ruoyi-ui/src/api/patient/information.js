import request from '@/utils/request'

export function listInformation(query) {
  return request({
    url: '/patient/information/list',
    method: 'get',
    params: query
  })
}

export function listMyInformation(query) {
  return request({
    url: '/patient/information/listMy',
    method: 'get',
    params: query
  })
}

export function getInformation(visitId) {
  return request({
    url: '/patient/information/' + visitId,
    method: 'get'
  })
}

export function addInformation(data) {
  return request({
    url: '/patient/information',
    method: 'post',
    data
  })
}

export function mobileSubmitInformation(data) {
  return request({
    url: '/patient/information/mobile/submit',
    method: 'post',
    data
  })
}

export function getMobileSubmitToken() {
  return request({
    url: '/patient/information/mobile/token',
    method: 'get'
  })
}

export function updateInformation(data) {
  return request({
    url: '/patient/information',
    method: 'put',
    data
  })
}

export function delInformation(visitId) {
  return request({
    url: '/patient/information/' + visitId,
    method: 'delete'
  })
}

export function importInformationExcel(data) {
  return request({
    url: '/patient/information/importData',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 10 * 60 * 1000,
    data
  })
}

export function listHospitalOptions() {
  return request({
    url: '/patient/information/hospitalOptions',
    method: 'get'
  })
}