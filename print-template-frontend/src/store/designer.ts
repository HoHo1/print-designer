import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Template, Element, PaperSettings } from '@/types/template'

export const useDesignerStore = defineStore('designer', () => {
  const template = ref<Template | null>(null)
  const selectedElementIds = ref<string[]>([])
  const zoom = ref(100)
  const gridSize = ref(10)
  const showGrid = ref(true)
  const showRuler = ref(true)
  const historyStack = ref<any[]>([])
  const redoStack = ref<any[]>([])

  const selectedElement = computed(() => {
    if (selectedElementIds.value.length !== 1 || !template.value) return null
    return findElement(template.value.elements, selectedElementIds.value[0])
  })

  function findElement(elements: Element[], id: string): Element | null {
    for (const el of elements) {
      if (el.id === id) return el
      if (el.children) {
        const found = findElement(el.children, id)
        if (found) return found
      }
    }
    return null
  }

  function setTemplate(newTemplate: Template) {
    template.value = JSON.parse(JSON.stringify(newTemplate))
    clearHistory()
  }

  function addElement(element: Element) {
    template.value?.elements.push(element)
    addHistory()
  }

  function deleteElement(elementId: string) {
    if (!template.value) return
    const idx = template.value.elements.findIndex(el => el.id === elementId)
    if (idx >= 0) {
      template.value.elements.splice(idx, 1)
      selectedElementIds.value = selectedElementIds.value.filter(id => id !== elementId)
      addHistory()
    }
  }

  function updateElement(elementId: string, updates: Partial<Element> | { paper?: PaperSettings }) {
    if (!template.value) return
    
    if (elementId === 'paper' && 'paper' in updates && updates.paper) {
      template.value.paper = updates.paper
      addHistory()
      return
    }
    
    const el = findElement(template.value.elements, elementId as string)
    if (!el) return
    Object.assign(el, updates)
    addHistory()
  }

  function setZoom(newZoom: number) {
    zoom.value = Math.max(25, Math.min(400, newZoom))
  }

  function addHistory() {
    if (!template.value) return
    historyStack.value.push(JSON.stringify(template.value))
    redoStack.value = []
    if (historyStack.value.length > 50) {
      historyStack.value.shift()
    }
  }

  function undo() {
    if (historyStack.value.length > 1) {
      redoStack.value.push(historyStack.value.pop()!)
      template.value = JSON.parse(historyStack.value[historyStack.value.length - 1])
    }
  }

  function redo() {
    if (redoStack.value.length > 0) {
      historyStack.value.push(redoStack.value.pop()!)
      template.value = JSON.parse(historyStack.value[historyStack.value.length - 1])
    }
  }

  function clearHistory() {
    historyStack.value = template.value ? [JSON.stringify(template.value)] : []
    redoStack.value = []
  }

  return {
    template,
    selectedElementIds,
    selectedElement,
    zoom,
    gridSize,
    showGrid,
    showRuler,
    historyStack,
    redoStack,
    setTemplate,
    addElement,
    deleteElement,
    updateElement,
    setZoom,
    undo,
    redo,
  }
})
