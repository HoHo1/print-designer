<template>
  <div class="template-designer">
    <div class="designer-header">
      <div class="header-left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-input
          v-model="templateName"
          placeholder="模板名称"
          class="name-input"
          size="large"
        />
      </div>
      <div class="header-right">
        <el-button @click="handlePaperSettings">
          <el-icon><Setting /></el-icon>
          纸张设置
        </el-button>
        <el-button @click="handlePreview">
          <el-icon><View /></el-icon>
          预览
        </el-button>
        <el-button type="primary" @click="handleSave">
          <el-icon><Check /></el-icon>
          保存
        </el-button>
      </div>
    </div>
    
    <DesignerToolbar />
    
    <div class="designer-content">
      <div class="left-panel">
        <ElementPanel />
      </div>
      
      <div class="canvas-container">
        <DesignerCanvas />
      </div>
      
      <div class="right-panel">
        <PropertyPanel />
      </div>
    </div>
    
    <el-dialog v-model="paperDialogVisible" title="纸张设置" width="500px">
      <el-form :model="paperForm" label-width="100px">
        <el-form-item label="纸张宽度">
          <el-input-number v-model="paperForm.width" :min="50" :max="1000" />
          <span style="margin-left: 8px">mm</span>
        </el-form-item>
        <el-form-item label="纸张高度">
          <el-input-number v-model="paperForm.height" :min="50" :max="1000" />
          <span style="margin-left: 8px">mm</span>
        </el-form-item>
        <el-form-item label="纸张方向">
          <el-radio-group v-model="paperForm.orientation">
            <el-radio value="portrait">纵向</el-radio>
            <el-radio value="landscape">横向</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="上边距">
          <el-input-number v-model="paperForm.margin.top" :min="0" :max="100" />
          <span style="margin-left: 8px">mm</span>
        </el-form-item>
        <el-form-item label="下边距">
          <el-input-number v-model="paperForm.margin.bottom" :min="0" :max="100" />
          <span style="margin-left: 8px">mm</span>
        </el-form-item>
        <el-form-item label="左边距">
          <el-input-number v-model="paperForm.margin.left" :min="0" :max="100" />
          <span style="margin-left: 8px">mm</span>
        </el-form-item>
        <el-form-item label="右边距">
          <el-input-number v-model="paperForm.margin.right" :min="0" :max="100" />
          <span style="margin-left: 8px">mm</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paperDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePaperSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Setting, View, Check } from '@element-plus/icons-vue'
import { useDesignerStore } from '@/store/designer'
import DesignerToolbar from '@/components/designer/DesignerToolbar.vue'
import DesignerCanvas from '@/components/designer/DesignerCanvas.vue'
import ElementPanel from '@/components/designer/ElementPanel.vue'
import PropertyPanel from '@/components/designer/PropertyPanel.vue'
import * as templateApi from '@/api/template'
import { createDefaultTemplate, type Template, type PaperSettings } from '@/types/template'

const router = useRouter()
const route = useRoute()
const store = useDesignerStore()

const templateName = ref('')
const paperDialogVisible = ref(false)
const paperForm = ref<PaperSettings>({
  width: 210,
  height: 297,
  orientation: 'portrait',
  margin: {
    top: 10,
    bottom: 10,
    left: 10,
    right: 10
  }
})

const templateId = computed(() => route.params.id as string)

const loadTemplate = async (id: string) => {
  try {
    const template = await templateApi.getTemplate(id)
    templateName.value = template.name
    store.setTemplate(template)
  } catch (error) {
    ElMessage.error('加载模板失败')
  }
}

const initNewTemplate = () => {
  const template = createDefaultTemplate('新模板')
  templateName.value = template.name
  store.setTemplate(template)
}

const handleBack = () => {
  router.push('/templates')
}

const handlePaperSettings = () => {
  if (store.template?.paper) {
    paperForm.value = JSON.parse(JSON.stringify(store.template.paper))
  }
  paperDialogVisible.value = true
}

const handlePaperSave = () => {
  if (store.template) {
    store.updateElement('paper', { paper: paperForm.value })
  }
  paperDialogVisible.value = false
}

const handlePreview = () => {
  if (templateId.value) {
    router.push(`/preview/${templateId.value}`)
  } else {
    ElMessage.warning('请先保存模板')
  }
}

const handleSave = async () => {
  if (!templateName.value) {
    ElMessage.warning('请输入模板名称')
    return
  }
  
  if (!store.template) {
    ElMessage.warning('模板数据异常')
    return
  }
  
  try {
    const templateData: Template = {
      ...store.template,
      name: templateName.value
    }
    
    if (templateId.value) {
      await templateApi.updateTemplate(templateId.value, templateData)
    } else {
      const res = await templateApi.createTemplate(templateData)
      router.replace(`/designer/${res.id}`)
    }
    
    ElMessage.success('保存成功')
    store.addHistory()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  if (templateId.value) {
    loadTemplate(templateId.value)
  } else {
    initNewTemplate()
  }
})
</script>

<style scoped lang="scss">
.template-designer {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  
  .designer-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    background: #fff;
    border-bottom: 1px solid #e4e7ed;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .name-input {
        width: 300px;
      }
    }
    
    .header-right {
      display: flex;
      gap: 8px;
    }
  }
  
  .designer-content {
    flex: 1;
    display: flex;
    overflow: hidden;
    
    .left-panel {
      width: 240px;
      flex-shrink: 0;
      border-right: 1px solid #e4e7ed;
      overflow: hidden;
    }
    
    .canvas-container {
      flex: 1;
      overflow: hidden;
    }
    
    .right-panel {
      width: 280px;
      flex-shrink: 0;
      border-left: 1px solid #e4e7ed;
      overflow: hidden;
    }
  }
}
</style>
