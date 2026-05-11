<template>
  <div class="element-panel">
    <div class="panel-section">
      <div class="section-title">基础元素</div>
      <div class="element-list">
        <div class="element-item" draggable="true" @dragstart="handleDragStart($event, 'text')">
          <el-icon><Document /></el-icon>
          <span>文本</span>
        </div>
        <div class="element-item" draggable="true" @dragstart="handleDragStart($event, 'image')">
          <el-icon><Picture /></el-icon>
          <span>图片</span>
        </div>
        <div class="element-item" draggable="true" @dragstart="handleDragStart($event, 'table')">
          <el-icon><Grid /></el-icon>
          <span>表格</span>
        </div>
        <div class="element-item" draggable="true" @dragstart="handleDragStart($event, 'barcode')">
          <el-icon><Tickets /></el-icon>
          <span>条码</span>
        </div>
        <div class="element-item" draggable="true" @dragstart="handleDragStart($event, 'qrcode')">
          <el-icon><Coin /></el-icon>
          <span>二维码</span>
        </div>
      </div>
    </div>
    <div class="panel-section">
      <div class="section-title">形状</div>
      <div class="element-list">
        <div class="element-item" draggable="true" @dragstart="handleDragStart($event, 'line')">
          <el-icon><Minus /></el-icon>
          <span>线条</span>
        </div>
        <div class="element-item" draggable="true" @dragstart="handleDragStart($event, 'rect')">
          <el-icon><Box /></el-icon>
          <span>矩形</span>
        </div>
        <div class="element-item" draggable="true" @dragstart="handleDragStart($event, 'circle')">
          <el-icon><CircleClose /></el-icon>
          <span>圆形</span>
        </div>
      </div>
    </div>
    <div class="panel-section" v-if="variables.length > 0">
      <div class="section-title">变量</div>
      <div class="variable-list">
        <div 
          v-for="variable in variables" 
          :key="variable.id"
          class="variable-item"
          draggable="true"
          @dragstart="handleVariableDragStart($event, variable)"
        >
          <el-tag size="small" type="info">{{ variable.label }}</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useDesignerStore } from '@/store/designer'
import { Document, Picture, Grid, Tickets, Coin, Minus, Box, CircleClose } from '@element-plus/icons-vue'
import type { TemplateVariable } from '@/types/template'

const emit = defineEmits<{
  (e: 'addElement', type: string, options?: any): void
}>()

const store = useDesignerStore()

const variables = computed<TemplateVariable[]>(() => {
  return store.template?.variables || []
})

const handleDragStart = (event: DragEvent, type: string) => {
  event.dataTransfer!.setData('elementType', type)
}

const handleVariableDragStart = (event: DragEvent, variable: TemplateVariable) => {
  event.dataTransfer!.setData('elementType', 'text')
  event.dataTransfer!.setData('variable', JSON.stringify(variable))
}
</script>

<style scoped lang="scss">
.element-panel {
  height: 100%;
  overflow-y: auto;
  background: #fff;
  border-right: 1px solid #e4e7ed;
}

.panel-section {
  padding: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 12px;
}

.element-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.element-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 12px 8px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: grab;
  transition: all 0.2s;
  font-size: 12px;
  color: #606266;

  &:hover {
    background: #ecf5ff;
    border-color: #409eff;
    color: #409eff;
  }

  &:active {
    cursor: grabbing;
  }
}

.variable-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.variable-item {
  cursor: grab;

  &:hover {
    .el-tag {
      background: #ecf5ff;
      border-color: #409eff;
      color: #409eff;
    }
  }

  &:active {
    cursor: grabbing;
  }
}
</style>
