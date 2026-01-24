import request from '@/utils/request'

// 查询鼻炎患者就诊信息主（包含文档中所有字段）列表
export function listInformation(query) {
  return request({
    url: '/patient/information/list',
    method: 'get',
    params: query
  })
}

// 查询我的鼻炎患者就诊信息列表
export function listMyInformation(query) {
  return request({
    url: '/patient/information/listMy',
    method: 'get',
    params: query
  })
}

// 查询鼻炎患者就诊信息主（包含文档中所有字段）详细
export function getInformation(visitId) {
  return request({
    url: '/patient/information/' + visitId,
    method: 'get'
  })
}

// 新增鼻炎患者就诊信息主（包含文档中所有字段）
export function addInformation(data) {
  return request({
    url: '/patient/information',
    method: 'post',
    data: data
  })
}

// 修改鼻炎患者就诊信息主（包含文档中所有字段）
export function updateInformation(data) {
  return request({
    url: '/patient/information',
    method: 'put',
    data: data
  })
}

// 删除鼻炎患者就诊信息主（包含文档中所有字段）
export function delInformation(visitId) {
  return request({
    url: '/patient/information/' + visitId,
    method: 'delete'
  })
}



// 导入鼻炎患者就诊信息（Excel）
export function importInformationExcel(data) {
  return request({
    url: '/patient/information/importData',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 10 * 60 * 1000,
    data
  })
}

// 获取医院下拉选项
export function listHospitalOptions() {
  return request({
    url: '/patient/information/hospitalOptions',
    method: 'get'
  })
}
