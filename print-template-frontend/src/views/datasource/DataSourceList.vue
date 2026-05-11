<template>
  <div class="data-source-list">
    <div class="header">
      <h1>数据源管理</h1>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新建数据源
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="类型">
          <el-select v-model="filterForm.type" placeholder="全部" clearable>
            <el-option label="静态数据" value="static" />
            <el-option label="API数据" value="api" />
            <el-option label="SQL数据" value="sql" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadDataSources">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="dataSources" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="数据源名称" min-width="200" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'static'">静态数据</el-tag>
            <el-tag v-else-if="row.type === 'api'" type="success">API数据</el-tag>
            <el-tag v-else type="warning">SQL数据</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleTest(row)">测试连接</el-button>
            <el-button link type="primary" @click="handlePreview(row)">预览数据</el-button>
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
          @size-change="loadDataSources"
          @current-change="loadDataSources"
        />
      </div>
    </el-card>

    <!-- 数据源编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入数据源名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="static">静态数据</el-radio>
            <el-radio label="api">API数据</el-radio>
            <el-radio label="sql">SQL数据</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        
        <!-- 静态数据 -->
        <template v-if="form.type === 'static'">
          <el-form-item label="数据内容" prop="staticData">
            <el-input
              v-model="form.staticData"
              type="textarea"
              :rows="8"
              placeholder="请输入JSON格式的静态数据"
            />
          </el-form-item>
        </template>
        
        <!-- API数据 -->
        <template v-if="form.type === 'api'">
          <el-form-item label="请求URL" prop="url">
            <el-input v-model="form.url" placeholder="请输入API地址" />
          </el-form-item>
          <el-form-item label="请求方法" prop="method">
            <el-radio-group v-model="form.method">
              <el-radio label="GET">GET</el-radio>
              <el-radio label="POST">POST</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="请求头" prop="headers">
            <el-input
              v-model="form.headers"
              type="textarea"
              :rows="4"
              placeholder="请输入JSON格式的请求头"
            />
          </el-form-item>
        </template>
        
        <!-- SQL数据 -->
        <template v-if="form.type === 'sql'">
          <el-form-item label="SQL配置" prop="config">
            <el-input
              v-model="form.config"
              type="textarea"
              :rows="8"
              placeholder="请输入数据库连接和SQL配置"
            />
          </el-form-item>
        </template>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 数据预览对话框 -->
    <el-dialog v-model="previewVisible" title="数据预览" width="700px">
      <pre v-if="previewData" class="preview-content">{{ JSON.stringify(previewData, null, 2) }}</pre>
      <el-empty v-else description="暂无数据" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import * as datasourceApi from '@/api/datasource'
import type { DataSource } from '@/api/datasource'

const loading = ref(false)
const dataSources = ref<DataSource[]>([])
const dialogVisible = ref(false)
const previewVisible = ref(false)
const previewData = ref<any>(null)
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const currentId = ref<number | null>(null)

const filterForm = ref({
  type: ''
})
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0
})

const form = ref<DataSource>({
  name: '',
  type: 'static',
  description: '',
  url: '',
  method: 'GET',
  headers: '',
  staticData: '',
  config: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入数据源名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择数据源类型', trigger: 'change' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑数据源' : '新建数据源')

const loadDataSources = async () => {
  loading.value = true
  try {
    const res = await datasourceApi.getDataSources({
      pageNum: pagination.value.page,
      pageSize: pagination.value.pageSize,
      type: filterForm.value.type
    })
    dataSources.value = res.records || []
    pagination.value.total = res.total || 0
  } catch (error) {
    ElMessage.error('加载数据源列表失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  isEdit.value = false
  currentId.value = null
  form.value = {
    name: '',
    type: 'static',
    description: '',
    url: '',
    method: 'GET',
    headers: '',
    staticData: '',
    config: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: DataSource) => {
  isEdit.value = true
  currentId.value = row.id!
  form.value = { ...row }
  dialogVisible.value = true
}

const handleTest = async (row: DataSource) => {
  try {
    const loading = ElMessage.loading('正在测试连接...')
    await datasourceApi.testDataSource(row.id!)
    loading.close()
    ElMessage.success('连接成功')
  } catch (error) {
    ElMessage.error('连接失败')
  }
}

const handlePreview = async (row: DataSource) => {
  try {
    const res = await datasourceApi.previewDataSource(row.id!)
    previewData.value = res.data
    previewVisible.value = true
  } catch (error) {
    ElMessage.error('数据预览失败')
  }
}

const handleDelete = async (row: DataSource) => {
  try {
    await ElMessageBox.confirm('确定要删除此数据源吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await datasourceApi.deleteDataSource(row.id!)
    ElMessage.success('删除成功')
    loadDataSources()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    if (isEdit.value && currentId.value) {
      await datasourceApi.updateDataSource({ ...form.value, id: currentId.value })
      ElMessage.success('更新成功')
    } else {
      await datasourceApi.createDataSource(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadDataSources()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const handleReset = () => {
  filterForm.value.type = ''
  pagination.value.page = 1
  loadDataSources()
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  loadDataSources()
})
</script>

<style scoped lang="scss">
.data-source-list {
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
  
  .preview-content {
    background: #f5f7fa;
    padding: 16px;
    border-radius: 4px;
    overflow: auto;
    max-height: 400px;
  }
}
</style>
