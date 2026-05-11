<template>
  <div class="template-list">
    <div class="header">
      <h1>模板管理</h1>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新建模板
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="搜索">
          <el-input v-model="filterForm.keyword" placeholder="模板名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="templates" style="width: 100%">
        <el-table-column prop="name" label="模板名称" min-width="200" />
        <el-table-column prop="code" label="模板编码" width="180" />
        <el-table-column prop="paperSize" label="纸张大小" width="120">
          <template #default="{ row }">
            {{ row.paper?.width }} x {{ row.paper?.height }} mm
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handlePreview(row)">预览</el-button>
            <el-button link type="primary" @click="handleCopy(row)">复制</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import * as templateApi from '@/api/template'
import type { Template } from '@/types/template'

const router = useRouter()

const templates = ref<Template[]>([])
const filterForm = ref({
  keyword: ''
})
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0
})

const loadTemplates = async () => {
  try {
    const res = await templateApi.getTemplates({
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      keyword: filterForm.value.keyword
    })
    templates.value = res.records || []
    pagination.value.total = res.total || 0
  } catch (error) {
    ElMessage.error('加载模板列表失败')
  }
}

const handleCreate = () => {
  router.push('/designer')
}

const handleEdit = (row: Template) => {
  router.push(`/designer/${row.id}`)
}

const handlePreview = (row: Template) => {
  router.push(`/preview/${row.id}`)
}

const handleCopy = async (row: Template) => {
  try {
    await templateApi.copyTemplate(row.id!)
    ElMessage.success('复制成功')
    loadTemplates()
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const handleDelete = async (row: Template) => {
  try {
    await ElMessageBox.confirm('确定要删除此模板吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await templateApi.deleteTemplate(row.id!)
    ElMessage.success('删除成功')
    loadTemplates()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSearch = () => {
  pagination.value.page = 1
  loadTemplates()
}

const handleReset = () => {
  filterForm.value.keyword = ''
  handleSearch()
}

onMounted(() => {
  loadTemplates()
})
</script>

<style scoped lang="scss">
.template-list {
  padding: 20px;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h1 {
      margin: 0;
      font-size: 24px;
    }
  }
  
  .filter-card {
    margin-bottom: 20px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
