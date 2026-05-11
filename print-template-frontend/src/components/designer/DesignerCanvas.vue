<template>
  <div class="designer-canvas-wrapper" ref="wrapperRef" @drop="handleDrop" @dragover="handleDragOver">
    <div v-if="showRuler" class="ruler-top" :style="rulerStyle">
      <div class="ruler-mark" v-for="i in rulerMarks" :key="i" :style="{ left: i * 10 + 'px' }">
        <span v-if="i % 10 === 0">{{ i }}</span>
      </div>
    </div>
    <div v-if="showRuler" class="ruler-left" :style="rulerStyle">
      <div class="ruler-mark" v-for="i in rulerMarks" :key="i" :style="{ top: i * 10 + 'px' }">
        <span v-if="i % 10 === 0">{{ i }}</span>
      </div>
    </div>
    <div class="canvas-container" ref="containerRef" :style="canvasStyle" @mousedown="handleContainerMouseDown">
      <div 
        class="paper" 
        :style="paperStyle"
        @mousedown.stop="handlePaperMouseDown"
      >
        <div v-if="showGrid" class="grid-overlay" :style="gridStyle"></div>
        <div class="elements-container">
          <component
            v-for="element in elements"
            :key="element.id"
            :is="getElementComponent(element.type)"
            :element="element"
            :selected="selectedElementIds.includes(element.id)"
            @select="handleElementSelect"
            @update="handleElementUpdate"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useDesignerStore } from '@/store/designer'
import { generateId } from '@/types/template'
import TextElement from '@/components/elements/TextElement.vue'
import ImageElement from '@/components/elements/ImageElement.vue'
import TableElement from '@/components/elements/TableElement.vue'
import BarcodeElement from '@/components/elements/BarcodeElement.vue'
import QrCodeElement from '@/components/elements/QrCodeElement.vue'
import ShapeElement from '@/components/elements/ShapeElement.vue'

const store = useDesignerStore()

const wrapperRef = ref<HTMLElement>()
const containerRef = ref<HTMLElement>()

const elements = computed(() => store.template?.elements || [])
const selectedElementIds = computed(() => store.selectedElementIds)
const zoom = computed(() => store.zoom)
const showGrid = computed(() => store.showGrid)
const showRuler = computed(() => store.showRuler)
const paper = computed(() => store.template?.paper)

const zoomFactor = computed(() => zoom.value / 100)

const rulerMarks = computed(() => {
  const max = Math.max(paper.value?.width || 210, paper.value?.height || 297)
  return Math.ceil(max / 10)
})

const rulerStyle = computed(() => ({
  transform: `scale(${zoomFactor.value})`,
  transformOrigin: 'top left'
}))

const canvasStyle = computed(() => ({
  transform: `scale(${zoomFactor.value})`,
  transformOrigin: 'top left'
}))

const paperStyle = computed(() => ({
  width: `${paper.value?.width}mm`,
  height: `${paper.value?.height}mm`,
  background: '#ffffff',
  boxShadow: '0 0 20px rgba(0,0,0,0.1)',
  position: 'relative',
  overflow: 'hidden'
}))

const gridStyle = computed(() => ({
  backgroundImage: `linear-gradient(#e4e7ed 1px, transparent 1px), linear-gradient(90deg, #e4e7ed 1px, transparent 1px)`,
  backgroundSize: `${store.gridSize}px ${store.gridSize}px`
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

function getElementComponent(type: string) {
  return elementComponents[type] || TextElement
}

function handleDragOver(event: DragEvent) {
  event.preventDefault()
}

function handleDrop(event: DragEvent) {
  event.preventDefault()
  const elementType = event.dataTransfer?.getData('elementType')
  if (!elementType || !paper.value) return

  const variableData = event.dataTransfer?.getData('variable')
  
  const rect = wrapperRef.value?.getBoundingClientRect()
  if (!rect) return

  const x = (event.clientX - rect.left) / zoomFactor.value
  const y = (event.clientY - rect.top - (showRuler.value ? 30 : 0)) / zoomFactor.value

  const newElement = createDefaultElement(elementType, x, y)
  
  if (variableData) {
    const variable = JSON.parse(variableData)
    newElement.options.text = `{{${variable.name}}}`
  }

  store.addElement(newElement)
  store.selectedElementIds = [newElement.id]
}

function createDefaultElement(type: string, x: number, y: number) {
  const base = {
    id: generateId(),
    type: type as any,
    x,
    y,
    rotate: 0,
    locked: false,
    visible: true,
    zIndex: elements.value.length,
    options: {}
  }

  switch (type) {
    case 'text':
      return {
        ...base,
        width: 150,
        height: 40,
        options: {
          text: '双击编辑文本',
          fontFamily: 'Arial',
          fontSize: 14,
          fontColor: '#000000',
          fontBold: false,
          fontItalic: false,
          textAlign: 'left'
        }
      }
    case 'image':
      return {
        ...base,
        width: 100,
        height: 100,
        options: {
          url: '',
          fit: 'contain'
        }
      }
    case 'table':
      return {
        ...base,
        width: 300,
        height: 150,
        options: {
          columns: 3,
          rows: 5,
          data: []
        }
      }
    case 'barcode':
      return {
        ...base,
        width: 200,
        height: 80,
        options: {
          content: '1234567890',
          format: 'CODE128',
          showText: true
        }
      }
    case 'qrcode':
      return {
        ...base,
        width: 100,
        height: 100,
        options: {
          content: 'https://example.com',
          color: '#000000'
        }
      }
    case 'line':
      return {
        ...base,
        width: 150,
        height: 2,
        options: {
          stroke: '#000000',
          strokeWidth: 2
        }
      }
    case 'rect':
      return {
        ...base,
        width: 100,
        height: 60,
        options: {
          fill: 'rgba(255,255,255,0)',
          stroke: '#000000',
          strokeWidth: 1
        }
      }
    case 'circle':
      return {
        ...base,
        width: 80,
        height: 80,
        options: {
          fill: 'rgba(255,255,255,0)',
          stroke: '#000000',
          strokeWidth: 1
        }
      }
    default:
      return base
  }
}

function handleElementSelect(id: string, event: MouseEvent) {
  if (event.shiftKey) {
    if (store.selectedElementIds.includes(id)) {
      store.selectedElementIds = store.selectedElementIds.filter(i => i !== id)
    } else {
      store.selectedElementIds.push(id)
    }
  } else {
    store.selectedElementIds = [id]
  }
}

function handleElementUpdate(id: string, updates: any) {
  store.updateElement(id, updates)
}

function handlePaperMouseDown(event: MouseEvent) {
  if ((event.target as HTMLElement).classList.contains('paper') || 
      (event.target as HTMLElement).classList.contains('grid-overlay')) {
    store.selectedElementIds = []
  }
}

function handleContainerMouseDown(event: MouseEvent) {
  if (event.target === containerRef.value) {
    store.selectedElementIds = []
  }
}

function handleKeyDown(event: KeyboardEvent) {
  if ((event.ctrlKey || event.metaKey) && event.key === 'z') {
    event.preventDefault()
    if (event.shiftKey) {
      store.redo()
    } else {
      store.undo()
    }
  } else if (event.key === 'Delete' || event.key === 'Backspace') {
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
}

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
})
</script>

<style scoped lang="scss">
.designer-canvas-wrapper {
  flex: 1;
  position: relative;
  overflow: auto;
  background: #dcdfe6;
  background-image: radial-gradient(circle, #c0c4cc 1px, transparent 1px);
  background-size: 20px 20px;
}

.ruler-top {
  position: sticky;
  top: 0;
  left: 0;
  height: 30px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  z-index: 10;
  width: 2000px;
}

.ruler-left {
  position: sticky;
  top: 30px;
  left: 0;
  width: 30px;
  background: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  z-index: 10;
  height: 2000px;
}

.ruler-mark {
  position: absolute;
  width: 1px;
  height: 5px;
  background: #909399;
  
  span {
    position: absolute;
    top: 6px;
    left: -4px;
    font-size: 10px;
    color: #909399;
    white-space: nowrap;
  }
}

.ruler-left .ruler-mark {
  width: 5px;
  height: 1px;
  
  span {
    top: -6px;
    left: 8px;
  }
}

.canvas-container {
  padding: 40px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100%;
  min-width: 100%;
}

.paper {
  flex-shrink: 0;
}

.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.elements-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
</style>
