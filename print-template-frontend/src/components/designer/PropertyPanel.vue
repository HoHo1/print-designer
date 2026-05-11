<template>
  <div class="property-panel">
    <div v-if="!selectedElement" class="empty-state">
      <el-empty description="请选择一个元素" />
    </div>
    <div v-else class="property-content">
      <el-divider content-position="left">位置与尺寸</el-divider>
      <el-form label-width="60px" size="small">
        <el-form-item label="X">
          <el-input-number v-model="xValue" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="Y">
          <el-input-number v-model="yValue" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="宽">
          <el-input-number v-model="widthValue" :min="10" :step="1" />
        </el-form-item>
        <el-form-item label="高">
          <el-input-number v-model="heightValue" :min="10" :step="1" />
        </el-form-item>
        <el-form-item label="旋转">
          <el-input-number v-model="rotateValue" :min="-180" :max="180" :step="1" />
        </el-form-item>
      </el-form>
      
      <el-divider content-position="left">样式</el-divider>
      <el-form label-width="60px" size="small">
        <el-form-item label="层级">
          <el-input-number v-model="zIndexValue" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="锁定">
          <el-switch v-model="lockedValue" />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch v-model="visibleValue" />
        </el-form-item>
      </el-form>

      <template v-if="selectedElement.type === 'text'">
        <el-divider content-position="left">文本属性</el-divider>
        <el-form label-width="60px" size="small">
          <el-form-item label="内容">
            <el-input v-model="textContent" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="字体">
            <el-select v-model="fontFamily">
              <el-option label="Arial" value="Arial" />
              <el-option label="Microsoft YaHei" value="Microsoft YaHei" />
              <el-option label="SimSun" value="SimSun" />
              <el-option label="SimHei" value="SimHei" />
            </el-select>
          </el-form-item>
          <el-form-item label="字号">
            <el-input-number v-model="fontSize" :min="8" :max="200" />
          </el-form-item>
          <el-form-item label="颜色">
            <el-color-picker v-model="fontColor" />
          </el-form-item>
          <el-form-item label="加粗">
            <el-switch v-model="fontBold" />
          </el-form-item>
          <el-form-item label="斜体">
            <el-switch v-model="fontItalic" />
          </el-form-item>
          <el-form-item label="对齐">
            <el-radio-group v-model="textAlign">
              <el-radio-button label="left">左</el-radio-button>
              <el-radio-button label="center">中</el-radio-button>
              <el-radio-button label="right">右</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </template>

      <template v-if="selectedElement.type === 'image'">
        <el-divider content-position="left">图片属性</el-divider>
        <el-form label-width="60px" size="small">
          <el-form-item label="URL">
            <el-input v-model="imageUrl" placeholder="请输入图片地址" />
          </el-form-item>
          <el-form-item label="填充">
            <el-select v-model="imageFit">
              <el-option label="包含" value="contain" />
              <el-option label="覆盖" value="cover" />
              <el-option label="填充" value="fill" />
              <el-option label="无" value="none" />
            </el-select>
          </el-form-item>
        </el-form>
      </template>

      <template v-if="selectedElement.type === 'barcode'">
        <el-divider content-position="left">条码属性</el-divider>
        <el-form label-width="60px" size="small">
          <el-form-item label="内容">
            <el-input v-model="barcodeContent" placeholder="请输入条码内容" />
          </el-form-item>
          <el-form-item label="格式">
            <el-select v-model="barcodeFormat">
              <el-option label="CODE128" value="CODE128" />
              <el-option label="EAN13" value="EAN13" />
              <el-option label="UPC" value="UPC" />
              <el-option label="CODE39" value="CODE39" />
            </el-select>
          </el-form-item>
          <el-form-item label="显示文本">
            <el-switch v-model="barcodeShowText" />
          </el-form-item>
        </el-form>
      </template>

      <template v-if="selectedElement.type === 'qrcode'">
        <el-divider content-position="left">二维码属性</el-divider>
        <el-form label-width="60px" size="small">
          <el-form-item label="内容">
            <el-input v-model="qrcodeContent" placeholder="请输入二维码内容" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="颜色">
            <el-color-picker v-model="qrcodeColor" />
          </el-form-item>
        </el-form>
      </template>

      <template v-if="['rect', 'circle', 'line'].includes(selectedElement.type)">
        <el-divider content-position="left">形状属性</el-divider>
        <el-form label-width="60px" size="small">
          <el-form-item label="填充">
            <el-color-picker v-model="shapeFill" show-alpha />
          </el-form-item>
          <el-form-item label="边框">
            <el-color-picker v-model="shapeStroke" />
          </el-form-item>
          <el-form-item label="宽度">
            <el-input-number v-model="shapeStrokeWidth" :min="0" :step="0.5" />
          </el-form-item>
        </el-form>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { useDesignerStore } from '@/store/designer'

const store = useDesignerStore()

const selectedElement = computed(() => store.selectedElement)

const xValue = computed({
  get: () => selectedElement.value?.x || 0,
  set: (val) => updateProperty('x', val)
})

const yValue = computed({
  get: () => selectedElement.value?.y || 0,
  set: (val) => updateProperty('y', val)
})

const widthValue = computed({
  get: () => selectedElement.value?.width || 0,
  set: (val) => updateProperty('width', val)
})

const heightValue = computed({
  get: () => selectedElement.value?.height || 0,
  set: (val) => updateProperty('height', val)
})

const rotateValue = computed({
  get: () => selectedElement.value?.rotate || 0,
  set: (val) => updateProperty('rotate', val)
})

const zIndexValue = computed({
  get: () => selectedElement.value?.zIndex || 0,
  set: (val) => updateProperty('zIndex', val)
})

const lockedValue = computed({
  get: () => selectedElement.value?.locked || false,
  set: (val) => updateProperty('locked', val)
})

const visibleValue = computed({
  get: () => selectedElement.value?.visible ?? true,
  set: (val) => updateProperty('visible', val)
})

const textContent = computed({
  get: () => selectedElement.value?.options?.text || '',
  set: (val) => updateOption('text', val)
})

const fontFamily = computed({
  get: () => selectedElement.value?.options?.fontFamily || 'Arial',
  set: (val) => updateOption('fontFamily', val)
})

const fontSize = computed({
  get: () => selectedElement.value?.options?.fontSize || 14,
  set: (val) => updateOption('fontSize', val)
})

const fontColor = computed({
  get: () => selectedElement.value?.options?.fontColor || '#000000',
  set: (val) => updateOption('fontColor', val)
})

const fontBold = computed({
  get: () => selectedElement.value?.options?.fontBold || false,
  set: (val) => updateOption('fontBold', val)
})

const fontItalic = computed({
  get: () => selectedElement.value?.options?.fontItalic || false,
  set: (val) => updateOption('fontItalic', val)
})

const textAlign = computed({
  get: () => selectedElement.value?.options?.textAlign || 'left',
  set: (val) => updateOption('textAlign', val)
})

const imageUrl = computed({
  get: () => selectedElement.value?.options?.url || '',
  set: (val) => updateOption('url', val)
})

const imageFit = computed({
  get: () => selectedElement.value?.options?.fit || 'contain',
  set: (val) => updateOption('fit', val)
})

const barcodeContent = computed({
  get: () => selectedElement.value?.options?.content || '',
  set: (val) => updateOption('content', val)
})

const barcodeFormat = computed({
  get: () => selectedElement.value?.options?.format || 'CODE128',
  set: (val) => updateOption('format', val)
})

const barcodeShowText = computed({
  get: () => selectedElement.value?.options?.showText || true,
  set: (val) => updateOption('showText', val)
})

const qrcodeContent = computed({
  get: () => selectedElement.value?.options?.content || '',
  set: (val) => updateOption('content', val)
})

const qrcodeColor = computed({
  get: () => selectedElement.value?.options?.color || '#000000',
  set: (val) => updateOption('color', val)
})

const shapeFill = computed({
  get: () => selectedElement.value?.options?.fill || 'rgba(255,255,255,0)',
  set: (val) => updateOption('fill', val)
})

const shapeStroke = computed({
  get: () => selectedElement.value?.options?.stroke || '#000000',
  set: (val) => updateOption('stroke', val)
})

const shapeStrokeWidth = computed({
  get: () => selectedElement.value?.options?.strokeWidth || 1,
  set: (val) => updateOption('strokeWidth', val)
})

function updateProperty(key: string, value: any) {
  if (selectedElement.value) {
    store.updateElement(selectedElement.value.id, { [key]: value })
  }
}

function updateOption(key: string, value: any) {
  if (selectedElement.value) {
    const newOptions = { ...selectedElement.value.options, [key]: value }
    store.updateElement(selectedElement.value.id, { options: newOptions })
  }
}
</script>

<style scoped lang="scss">
.property-panel {
  height: 100%;
  overflow-y: auto;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  padding: 16px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.property-content {
  :deep(.el-divider__text) {
    font-size: 12px;
    color: #909399;
    background: #fff;
  }

  :deep(.el-form-item) {
    margin-bottom: 12px;
  }

  :deep(.el-form-item__label) {
    font-size: 12px;
    color: #606266;
  }
}
</style>
