# 在线打印模板管理系统

一个功能完整的在线打印模板管理系统，支持可视化模板设计、多种数据源配置、打印预览和导出功能。

## 技术栈

### 后端
- Spring Boot 3.2
- MyBatis-Plus
- MySQL 8.0
- Redis
- Knife4j (API文档)
- ZXing (条码生成)
- OpenPDF (PDF导出)

### 前端
- Vue 3
- TypeScript
- Element Plus
- Pinia (状态管理)
- Vue Router
- html2canvas
- jsPDF

## 快速开始

### 使用 Docker Compose

1. 克隆项目到本地
2. 在项目根目录运行：
```bash
docker-compose up -d
```
3. 访问应用：
   - 前端: http://localhost
   - API文档: http://localhost:8080/api/doc.html

### 本地开发

#### 后端
```bash
cd print-template-backend
# 配置 application.yml 中的数据库和Redis连接
mvn spring-boot:run
```

#### 前端
```bash
cd print-template-frontend
npm install
npm run dev
```

## 功能特性

- 📝 **模板管理**: 模板的创建、编辑、删除、复制、导入导出
- 🎨 **可视化设计器**: 拖拽式模板设计，支持文本、图片、表格、条码、二维码、形状等元素
- 🔌 **数据源配置**: 支持静态数据、API数据、SQL数据多种数据源类型
- 🖨️ **打印管理**: 打印任务管理、打印预览、重新打印
- 📄 **导出功能**: 支持导出为PDF和图片
- 📱 **响应式设计**: 适配不同屏幕尺寸

## 项目结构

```
print-template-management/
├── print-template-backend/    # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/printtemplate/
│   │       │   ├── common/    # 公共模块
│   │       │   ├── config/    # 配置类
│   │       │   ├── module/    # 业务模块
│   │       │   │   ├── datasource/  # 数据源模块
│   │       │   │   ├── print/       # 打印任务模块
│   │       │   │   └── template/    # 模板模块
│   │       │   └── util/    # 工具类
│   │       └── resources/
│   │           ├── sql/     # SQL脚本
│   │           └── application.yml
│   └── Dockerfile
├── print-template-frontend/  # 前端项目
│   ├── src/
│   │   ├── api/       # API接口
│   │   ├── components/ # 组件
│   │   ├── views/     # 页面
│   │   ├── store/     # 状态管理
│   │   ├── router/    # 路由
│   │   ├── utils/     # 工具函数
│   │   └── types/     # 类型定义
│   ├── Dockerfile
│   └── nginx.conf
└── docker-compose.yml
```

## 许可证

MIT License
