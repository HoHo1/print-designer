<template>
  <div class="designer-toolbar">
    <el-button-group>
      <el-button size="small" @click="handleUndo" :disabled="!canUndo">
        <el-icon><RefreshLeft /></el-icon>
        撤销
      </el-button>
      <el-button size="small" @click="handleRedo" :disabled="!canRedo">
        <el-icon><RefreshRight /></el-icon>
        重做
      </el-button>
    </el-button-group>
    <el-divider direction="vertical" />
    <el-button-group>
      <el-button size="small" @click="handleZoomOut">
        <el-icon><ZoomOut /></el-icon>
      </el-button>
      <el-select v-model="zoomValue" size="small" style="width: 80px" @change="handleZoomChange">
        <el-option label="25%" :value="25" />
        <el-option label="50%" :value="50" />
        <el-option label="75%" :value="75" />
        <el-option label="100%" :value="100" />
        <el-option label="150%" :value="150" />
        <el-option label="200%" :value="200" />
        <el-option label="300%" :value="300" />
        <el-option label="400%" :value="400" />
      </el-select>
      <el-button size="small" @click="handleZoomIn">
        <el-icon><ZoomIn /></el-icon>
      </el-button>
    </el-button-group>
    <el-divider direction="vertical" />
    <el-button-group>
      <el-button size="small" @click="handleAlignLeft">
        <el-icon><DArrowLeft /></el-icon>
      </el-button>
      <el-button size="small" @click="handleAlignCenter">
        <el-icon><Minus /></el-icon>
      </el-button>
      <el-button size="small" @click="handleAlignRight">
        <el-icon><DArrowRight /></el-icon>
      </el-button>
      <el-button size="small" @click="handleAlignTop">
        <el-icon><DArrowUp /></el-icon>
      </el-button>
      <el-button size="small" @click="handleAlignMiddle">
        <el-icon><Minus /></el-icon>
      </el-button>
      <el-button size="small" @click="handleAlignBottom">
        <el-icon><DArrowDown /></el-icon>
      </el-button>
    </el-button-group>
    <el-divider direction="vertical" />
    <el-button-group>
      <el-button size="small" @click="handleCopy">
        <el-icon><DocumentCopy /></el-icon>
      </el-button>
      <el-button size="small" @click="handlePaste">
        <el-icon><DocumentAdd /></el-icon>
      </el-button>
      <el-button size="small" @click="handleDelete" :disabled="!hasSelection" type="danger">
        <el-icon><Delete /></el-icon>
      </el-button>
    </el-button-group>
    <el-divider direction="vertical" />
    <el-checkbox v-model="showGrid" size="small">网格</el-checkbox>
    <el-checkbox v-model="showRuler" size="small">标尺</el-checkbox>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useDesignerStore } from '@/store/designer'
import { RefreshLeft, RefreshRight, ZoomOut, ZoomIn, DArrowLeft, DArrowRight, DArrowUp, DArrowDown, Minus, DocumentCopy, DocumentAdd, Delete } from '@element-plus/icons-vue'

const store = useDesignerStore()

const zoomValue = computed({
  get: () => store.zoom,
  set: (val) => store.setZoom(val)
})

const showGrid = computed({
  get: () => store.showGrid,
  set: (val) => store.showGrid = val
})

const showRuler = computed({
  get: () => store.showRuler,
  set: (val) => store.showRuler = val
})

const canUndo = computed(() => store.historyStack.length > 1)
const canRedo = computed(() => store.redoStack.length > 0)
const hasSelection = computed(() => store.selectedElementIds.length > 0)

const handleUndo = () => store.undo()
const handleRedo = () => store.redo()
const handleZoomIn = () => store.setZoom(store.zoom + 25)
const handleZoomOut = () => store.setZoom(store.zoom - 25)
const handleZoomChange = (val: number) => store.setZoom(val)
const handleAlignLeft = () => {}
const handleAlignCenter = () => {}
const handleAlignRight = () => {}
const handleAlignTop = () => {}
const handleAlignMiddle = () => {}
const handleAlignBottom = () => {}
const handleCopy = () => {}
const handlePaste = () => {}
const handleDelete = () => {
  if (store.selectedElementIds.length > 0 && store.template) {
    store.selectedElementIds.forEach(id => {
      const idx = store.template!.elements.findIndex(el => el.id === id)
      if (idx >= 0) {
        store.template!.elements.splice(idx, 1)
      }
    })
    store.selectedElementIds = []
    store.addHistory()
  }
}
</script>

<style scoped lang="scss">
.designer-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}
</style>
