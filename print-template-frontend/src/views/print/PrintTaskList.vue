<template>
  <div class="print-task-list">
    <div class="header">
      <h1>打印任务</h1>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable>
            <el-option label="待打印" :value="0" />
            <el-option label="打印成功" :value="1" />
            <el-option label="打印失败" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadPrintTasks">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="printTasks" style="width: 100%" v-loading="loading">
        <el-table-column prop="taskNo" label="任务编号" width="200" />
        <el-table-column prop="templateName" label="模板名称" min-width="200" />
        <el-table-column prop="printerName" label="打印机" width="150" />
        <el-table-column prop="copies" label="份数" width="80" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">待打印</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">打印成功</el-tag>
            <el-tag v-else type="danger">打印失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="printTime" label="打印时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleReprint(row)" v-if="row.status !== 0">重新打印</el-button>
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
          @size-change="loadPrintTasks"
          @current-change="loadPrintTasks"
        />
      </div>
    </el-card>

    <!-- 查看任务详情对话框 -->
    <el-dialog v-model="detailVisible" title="任务详情" width="600px">
      <el-descriptions :column="1" border v-if="currentTask">
        <el-descriptions-item label="任务编号">{{ currentTask.taskNo }}</el-descriptions-item>
        <el-descriptions-item label="模板名称">{{ currentTask.templateName }}</el-descriptions-item>
        <el-descriptions-item label="打印机">{{ currentTask.printerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="纸张大小">{{ currentTask.paperSize || '-' }}</el-descriptions-item>
        <el-descriptions-item label="方向">{{ currentTask.orientation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="份数">{{ currentTask.copies }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentTask.status === 0" type="info">待打印</el-tag>
          <el-tag v-else-if="currentTask.status === 1" type="success">打印成功</el-tag>
          <el-tag v-else type="danger">打印失败</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="失败原因" v-if="currentTask.failReason">
          {{ currentTask.failReason }}
        </el-descriptions-item>
        <el-descriptions-item label="打印时间">{{ currentTask.printTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTask.createTime }}</el-descriptions-item>
        <el-descriptions-item label="数据内容">
          <pre class="data-content">{{ currentTask.dataContent ? JSON.stringify(JSON.parse(currentTask.dataContent), null, 2) : '-' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as printTaskApi from '@/api/print-task'
import type { PrintTask } from '@/api/print-task'

const loading = ref(false)
const printTasks = ref<PrintTask[]>([])
const detailVisible = ref(false)
const currentTask = ref<PrintTask | null>(null)

const filterForm = ref({
  status: undefined as number | undefined
})
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0
})

const loadPrintTasks = async () => {
  loading.value = true
  try {
    const res = await printTaskApi.getPrintTasks({
      pageNum: pagination.value.page,
      pageSize: pagination.value.pageSize,
      status: filterForm.value.status
    })
    printTasks.value = res.records || []
    pagination.value.total = res.total || 0
  } catch (error) {
    ElMessage.error('加载打印任务列表失败')
  } finally {
    loading.value = false
  }
}

const handleView = (row: PrintTask) => {
  currentTask.value = row
  detailVisible.value = true
}

const handleReprint = async (row: PrintTask) => {
  try {
    await ElMessageBox.confirm('确定要重新打印此任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await printTaskApi.updatePrintTaskStatus(row.id!, 0)
    ElMessage.success('已重新提交打印任务')
    loadPrintTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row: PrintTask) => {
  try {
    await ElMessageBox.confirm('确定要删除此任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await printTaskApi.deletePrintTask(row.id!)
    ElMessage.success('删除成功')
    loadPrintTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleReset = () => {
  filterForm.value.status = undefined
  pagination.value.page = 1
  loadPrintTasks()
}

onMounted(() => {
  loadPrintTasks()
})
</script>

<style scoped lang="scss">
.print-task-list {
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
  
  .data-content {
    background: #f5f7fa;
    padding: 12px;
    border-radius: 4px;
    overflow: auto;
    max-height: 300px;
    margin: 0;
  }
}
</style>
