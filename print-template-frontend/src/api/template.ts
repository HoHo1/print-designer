import request from '@/utils/request'
import type { Template } from '@/types/template'
import { createDefaultTemplate, generateId } from '@/types/template'

export async function getTemplates(params: any) {
  return request({
    url: '/template/list',
    method: 'get',
    params: {
      pageNum: params.page,
      pageSize: params.pageSize,
      keyword: params.keyword
    }
  })
}

export async function getTemplate(id: number) {
  return request({
    url: `/template/${id}`,
    method: 'get'
  })
}

export async function createTemplate(data: Partial<Template>) {
  return request({
    url: '/template',
    method: 'post',
    data
  })
}

export async function updateTemplate(data: Partial<Template>) {
  return request({
    url: '/template',
    method: 'put',
    data
  })
}

export async function deleteTemplate(id: number) {
  return request({
    url: `/template/${id}`,
    method: 'delete'
  })
}

export async function copyTemplate(id: number) {
  return request({
    url: `/template/${id}/copy`,
    method: 'post'
  })
}

export function exportTemplate(id: number) {
  return request({
    url: `/template/${id}/export`,
    method: 'get',
    responseType: 'blob'
  })
}

export function importTemplate(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/template/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function previewTemplate(id: number, data: any) {
  return request({
    url: `/template/${id}/preview`,
    method: 'post',
    data
  })
}
