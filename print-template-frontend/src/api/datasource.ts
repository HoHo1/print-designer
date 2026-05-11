import request from '@/utils/request'

export interface DataSource {
  id?: number
  name: string
  type: 'static' | 'api' | 'sql'
  description?: string
  url?: string
  method?: 'GET' | 'POST'
  headers?: string
  staticData?: string
  config?: string
  createTime?: string
  updateTime?: string
}

export async function getDataSources(params: any) {
  return request({
    url: '/datasource/list',
    method: 'get',
    params
  })
}

export async function getDataSource(id: number) {
  return request({
    url: `/datasource/${id}`,
    method: 'get'
  })
}

export async function createDataSource(data: DataSource) {
  return request({
    url: '/datasource',
    method: 'post',
    data
  })
}

export async function updateDataSource(data: DataSource) {
  return request({
    url: '/datasource',
    method: 'put',
    data
  })
}

export async function deleteDataSource(id: number) {
  return request({
    url: `/datasource/${id}`,
    method: 'delete'
  })
}

export async function testDataSource(id: number) {
  return request({
    url: `/datasource/${id}/test`,
    method: 'post'
  })
}

export async function previewDataSource(id: number, params?: any) {
  return request({
    url: `/datasource/${id}/preview`,
    method: 'post',
    data: params
  })
}
