import request from '@/utils/request'

export interface PrintTask {
  id?: number
  taskNo?: string
  templateId: number
  templateName: string
  dataContent?: string
  printerName?: string
  paperSize?: string
  orientation?: string
  copies?: number
  duplex?: number
  status?: number
  failReason?: string
  printTime?: string
  createBy?: string
  createTime?: string
  updateTime?: string
}

export async function getPrintTasks(params: any) {
  return request({
    url: '/print-task/list',
    method: 'get',
    params
  })
}

export async function getPrintTask(id: number) {
  return request({
    url: `/print-task/${id}`,
    method: 'get'
  })
}

export async function createPrintTask(data: PrintTask) {
  return request({
    url: '/print-task',
    method: 'post',
    data
  })
}

export async function updatePrintTaskStatus(id: number, status: number, failReason?: string) {
  return request({
    url: `/print-task/${id}/status`,
    method: 'put',
    params: { status, failReason }
  })
}

export async function deletePrintTask(id: number) {
  return request({
    url: `/print-task/${id}`,
    method: 'delete'
  })
}
