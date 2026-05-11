<template>
  <div
    class="element-wrapper text-element"
    :class="{ selected, locked, visible: !visible, preview }"
    :style="wrapperStyle"
    @mousedown.stop="!preview && handleMouseDown"
  >
    <div class="element-content" :style="contentStyle">
      <div v-if="isEditing && !preview" class="editor">
        <textarea
          ref="textareaRef"
          v-model="localText"
          @blur="handleBlur"
          @keydown.enter.stop.prevent="handleBlur"
        ></textarea>
      </div>
      <div v-else class="text-display" @dblclick="!preview && handleDoubleClick">
        <span v-html="renderedText"></span>
      </div>
    </div>
    <div v-if="selected && !locked && !preview" class="resize-handles">
      <div class="handle nw" @mousedown.stop="startResize('nw', $event)"></div>
      <div class="handle n" @mousedown.stop="startResize('n', $event)"></div>
      <div class="handle ne" @mousedown.stop="startResize('ne', $event)"></div>
      <div class="handle e" @mousedown.stop="startResize('e', $event)"></div>
      <div class="handle se" @mousedown.stop="startResize('se', $event)"></div>
      <div class="handle s" @mousedown.stop="startResize('s', $event)"></div>
      <div class="handle sw" @mousedown.stop="startResize('sw', $event)"></div>
      <div class="handle w" @mousedown.stop="startResize('w', $event)"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import type { Element } from '@/types/template'

interface Props {
  element: Element
  selected: boolean
  preview?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  preview: false
})
const emit = defineEmits<{
  (e: 'select', id: string, event: MouseEvent): void
  (e: 'update', id: string, updates: Partial<Element>): void
}>()

const isEditing = ref(false)
const localText = ref('')
const textareaRef = ref<HTMLTextAreaElement>()

const wrapperStyle = computed(() => ({
  position: 'absolute' as const,
  left: `${props.element.x}px`,
  top: `${props.element.y}px`,
  width: `${props.element.width}px`,
  height: `${props.element.height}px`,
  zIndex: props.element.zIndex,
  transform: `rotate(${props.element.rotate}deg)`,
  opacity: props.element.visible ? 1 : 0.3
}))

const contentStyle = computed(() => ({
  width: '100%',
  height: '100%',
  fontFamily: props.element.options.fontFamily || 'Arial',
  fontSize: `${props.element.options.fontSize || 14}px`,
  color: props.element.options.fontColor || '#000000',
  fontWeight: props.element.options.fontBold ? 'bold' : 'normal',
  fontStyle: props.element.options.fontItalic ? 'italic' : 'normal',
  textAlign: props.element.options.textAlign || 'left',
  display: 'flex',
  alignItems: 'center',
  justifyContent: props.element.options.textAlign || 'flex-start',
  padding: '4px',
  boxSizing: 'border-box' as const
}))

const renderedText = computed(() => {
  let text = props.element.options.text || ''
  text = text.replace(/\n/g, '<br>')
  return text
})

let isDragging = false
let isResizing = false
let startX = 0
let startY = 0
let startElementX = 0
let startElementY = 0
let startWidth = 0
let startHeight = 0
let resizeDir = ''

function handleMouseDown(event: MouseEvent) {
  if (props.element.locked) return
  emit('select', props.element.id, event)
  
  isDragging = true
  startX = event.clientX
  startY = event.clientY
  startElementX = props.element.x
  startElementY = props.element.y
  
  document.addEventListener('mousemove', handleDragMove)
  document.addEventListener('mouseup', handleDragEnd)
}

function handleDragMove(event: MouseEvent) {
  if (!isDragging) return
  const dx = event.clientX - startX
  const dy = event.clientY - startY
  emit('update', props.element.id, {
    x: startElementX + dx,
    y: startElementY + dy
  })
}

function handleDragEnd() {
  isDragging = false
  document.removeEventListener('mousemove', handleDragMove)
  document.removeEventListener('mouseup', handleDragEnd)
}

function startResize(dir: string, event: MouseEvent) {
  isResizing = true
  resizeDir = dir
  startX = event.clientX
  startY = event.clientY
  startElementX = props.element.x
  startElementY = props.element.y
  startWidth = props.element.width
  startHeight = props.element.height
  
  document.addEventListener('mousemove', handleResizeMove)
  document.addEventListener('mouseup', handleResizeEnd)
}

function handleResizeMove(event: MouseEvent) {
  if (!isResizing) return
  const dx = event.clientX - startX
  const dy = event.clientY - startY
  
  let updates: Partial<Element> = {}
  
  if (resizeDir.includes('e')) {
    updates.width = Math.max(20, startWidth + dx)
  }
  if (resizeDir.includes('w')) {
    const newWidth = Math.max(20, startWidth - dx)
    updates.width = newWidth
    updates.x = startElementX + (startWidth - newWidth)
  }
  if (resizeDir.includes('s')) {
    updates.height = Math.max(20, startHeight + dy)
  }
  if (resizeDir.includes('n')) {
    const newHeight = Math.max(20, startHeight - dy)
    updates.height = newHeight
    updates.y = startElementY + (startHeight - newHeight)
  }
  
  emit('update', props.element.id, updates)
}

function handleResizeEnd() {
  isResizing = false
  document.removeEventListener('mousemove', handleResizeMove)
  document.removeEventListener('mouseup', handleResizeEnd)
}

function handleDoubleClick() {
  if (props.element.locked) return
  localText.value = props.element.options.text || ''
  isEditing.value = true
  nextTick(() => {
    textareaRef.value?.focus()
  })
}

function handleBlur() {
  isEditing.value = false
  emit('update', props.element.id, {
    options: { ...props.element.options, text: localText.value }
  })
}
</script>

<style scoped lang="scss">
.element-wrapper {
  cursor: move;
  user-select: none;
  
  &.locked {
    cursor: not-allowed;
  }
}

.element-content {
  width: 100%;
  height: 100%;
}

.text-display {
  width: 100%;
  height: 100%;
  word-break: break-word;
  white-space: pre-wrap;
}

.editor {
  width: 100%;
  height: 100%;
  
  textarea {
    width: 100%;
    height: 100%;
    border: 1px dashed #409eff;
    background: rgba(64, 158, 255, 0.1);
    resize: none;
    outline: none;
    font: inherit;
    padding: 4px;
    box-sizing: border-box;
  }
}

.resize-handles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  
  .handle {
    position: absolute;
    width: 8px;
    height: 8px;
    background: #409eff;
    border: 1px solid #fff;
    pointer-events: auto;
    
    &.nw { top: -4px; left: -4px; cursor: nw-resize; }
    &.n { top: -4px; left: 50%; transform: translateX(-50%); cursor: n-resize; }
    &.ne { top: -4px; right: -4px; cursor: ne-resize; }
    &.e { top: 50%; right: -4px; transform: translateY(-50%); cursor: e-resize; }
    &.se { bottom: -4px; right: -4px; cursor: se-resize; }
    &.s { bottom: -4px; left: 50%; transform: translateX(-50%); cursor: s-resize; }
    &.sw { bottom: -4px; left: -4px; cursor: sw-resize; }
    &.w { top: 50%; left: -4px; transform: translateY(-50%); cursor: w-resize; }
  }
}

.text-element {
  &.selected {
    outline: 2px solid #409eff;
  }
}
</style>
