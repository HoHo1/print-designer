export interface Element {
  id: string
  type: 'text' | 'image' | 'table' | 'barcode' | 'qrcode' | 'line' | 'rect' | 'circle'
  x: number
  y: number
  width: number
  height: number
  rotate: number
  locked: boolean
  visible: boolean
  zIndex: number
  options: any
  children?: Element[]
}

export interface PaperSettings {
  width: number
  height: number
  orientation: 'portrait' | 'landscape'
  margin: {
    top: number
    bottom: number
    left: number
    right: number
  }
}

export interface Template {
  id?: string
  name: string
  code?: string
  paper: PaperSettings
  elements: Element[]
  variables?: TemplateVariable[]
  datasource?: any
  createTime?: string
  updateTime?: string
}

export interface TemplateVariable {
  id: string
  name: string
  label: string
  type: 'string' | 'number' | 'date' | 'array'
  group?: string
  defaultValue?: any
  required: boolean
  validationRule?: any
  sort: number
}

export function createDefaultTemplate(name: string = '新模板'): Template {
  return {
    name,
    paper: {
      width: 210,
      height: 297,
      orientation: 'portrait',
      margin: {
        top: 10,
        bottom: 10,
        left: 10,
        right: 10
      }
    },
    elements: []
  }
}

export function generateId(): string {
  return 'el_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}
