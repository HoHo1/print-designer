<template>
  <div class="template-preview">
    <div class="preview-header">
      <el-button @click="handleBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h1>{{ template?.name }}</h1>
      <div class="header-actions">
        <el-button @click="handlePrint">
          <el-icon><Printer /></el-icon>
          打印
        </el-button>
        <el-button type="primary" @click="handleExportPdf">
          <el-icon><Download /></el-icon>
          导出PDF
        </el-button>
      </div>
    </div>
    
    <div class="preview-content">
      <div class="preview-wrapper" ref="previewWrapper">
        <div
          class="paper-preview"
          :style="paperStyle"
        >
          <div class="elements-container">
            <component
              v-for="element in template?.elements"
              :key="element.id"
              :is="getElementComponent(element.type)"
              :element="element"
              :selected="false"
              :preview="true"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Printer, Download } from '@element-plus/icons-vue'
import * as templateApi from '@/api/template'
import type { Template } from '@/types/template'
import TextElement from '@/components/elements/TextElement.vue'
import ImageElement from '@/components/elements/ImageElement.vue'
import TableElement from '@/components/elements/TableElement.vue'
import BarcodeElement from '@/components/elements/BarcodeElement.vue'
import QrCodeElement from '@/components/elements/QrCodeElement.vue'
import ShapeElement from '@/components/elements/ShapeElement.vue'

const router = useRouter()
const route = useRoute()

const template = ref<Template | null>(null)
const previewWrapper = ref<HTMLElement>()

const templateId = computed(() => route.params.id as string)

const paperStyle = computed(() => ({
  width: `${template.value?.paper?.width}mm`,
  height: `${template.value?.paper?.height}mm`,
  background: '#fff',
  boxShadow: '0 2px 12px rgba(0,0,0,0.1)',
  position: 'relative' as const
}))

const elementComponents: Record<string, any> = {
  text: TextElement,
  image: ImageElement,
  table: TableElement,
  barcode: BarcodeElement,
  qrcode: QrCodeElement,
  line: ShapeElement,
  rect: ShapeElement,
  circle: ShapeElement
}

const getElementComponent = (type: string) => {
  return elementComponents[type] || TextElement
}

const loadTemplate = async (id: string) => {
  try {
    template.value = await templateApi.getTemplate(id)
  } catch (error) {
    ElMessage.error('加载模板失败')
  }
}

const handleBack = () => {
  router.push('/templates')
}

const handlePrint = () => {
  window.print()
}

const handleExportPdf = async () => {
  try {
    const { jsPDF } = await import('jspdf')
    const html2canvas = await import('html2canvas')
    
    if (!previewWrapper.value || !template.value) return
    
    const canvas = await html2canvas.default(previewWrapper.value, {
      scale: 2,
      useCORS: true
    })
    
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF({
      orientation: template.value.paper?.orientation === 'landscape' ? 'l' : 'p',
      unit: 'mm',
      format: [template.value.paper?.width || 210, template.value.paper?.height || 297]
    })
    
    const imgWidth = pdf.internal.pageSize.getWidth()
    const imgHeight = (canvas.height * imgWidth) / canvas.width
    
    pdf.addImage(imgData, 'PNG', 0, 0, imgWidth, imgHeight)
    pdf.save(`${template.value.name}.pdf`)
    
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  loadTemplate(templateId.value)
})
</script>

<style scoped lang="scss">
.template-preview {
  display: flex;
  flex-direction: column;
  height: 100vh;
  
  .preview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    background: #fff;
    border-bottom: 1px solid #e4e7ed;
    
    h1 {
      margin: 0;
      font-size: 20px;
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
    }
  }
  
  .preview-content {
    flex: 1;
    overflow: auto;
    padding: 40px;
    background: #dcdfe6;
    background-image: radial-gradient(circle, #c0c4cc 1px, transparent 1px);
    background-size: 20px 20px;
    
    .preview-wrapper {
      display: flex;
      justify-content: center;
      align-items: flex-start;
    }
    
    .paper-preview {
      flex-shrink: 0;
    }
    
    .elements-container {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
    }
  }
}

@media print {
  .preview-header {
    display: none !important;
  }
  
  .preview-content {
    padding: 0 !important;
    background: none !important;
    overflow: visible !important;
  }
  
  .paper-preview {
    box-shadow: none !important;
  }
}
</style>
