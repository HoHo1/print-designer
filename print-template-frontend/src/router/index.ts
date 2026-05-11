import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/templates'
  },
  {
    path: '/templates',
    name: 'TemplateList',
    component: () => import('@/views/template/TemplateList.vue')
  },
  {
    path: '/designer/:id?',
    name: 'TemplateDesigner',
    component: () => import('@/views/template/TemplateDesigner.vue')
  },
  {
    path: '/preview/:id',
    name: 'TemplatePreview',
    component: () => import('@/views/template/TemplatePreview.vue')
  },
  {
    path: '/datasources',
    name: 'DataSourceList',
    component: () => import('@/views/datasource/DataSourceList.vue')
  },
  {
    path: '/tasks',
    name: 'PrintTaskList',
    component: () => import('@/views/print/PrintTaskList.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
