-- 在线打印模板管理系统数据库脚本
-- 版本：V1.0
-- 日期：2026-05-11

-- 创建数据库
CREATE DATABASE IF NOT EXISTS print_template DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE print_template;

-- 1. 模板分类表
CREATE TABLE sys_template_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0为顶级',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板分类表';

-- 2. 模板主表
CREATE TABLE sys_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    code VARCHAR(50) NOT NULL COMMENT '模板编码，唯一',
    category_id BIGINT COMMENT '分类ID',
    thumbnail VARCHAR(500) COMMENT '缩略图路径',
    content LONGTEXT COMMENT '模板JSON内容',
    version INT DEFAULT 1 COMMENT '当前版本号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认模板：0-否，1-是',
    use_count INT DEFAULT 0 COMMENT '使用次数',
    create_by VARCHAR(50) COMMENT '创建人标识',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人标识',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE INDEX uk_code (code),
    INDEX idx_category_status (category_id, status),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板主表';

-- 3. 模板版本表
CREATE TABLE sys_template_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    template_id BIGINT NOT NULL COMMENT '模板ID',
    version INT NOT NULL COMMENT '版本号',
    content LONGTEXT NOT NULL COMMENT '模板JSON内容快照',
    change_log VARCHAR(500) COMMENT '变更说明',
    create_by VARCHAR(50) COMMENT '创建人标识',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE INDEX uk_template_version (template_id, version),
    INDEX idx_template_id (template_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板版本表';

-- 4. 模板变量表
CREATE TABLE sys_template_variable (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    template_id BIGINT NOT NULL COMMENT '模板ID',
    name VARCHAR(50) NOT NULL COMMENT '变量名',
    label VARCHAR(50) NOT NULL COMMENT '显示名称',
    type VARCHAR(20) DEFAULT 'string' COMMENT '变量类型：string/number/date/array',
    group_name VARCHAR(50) COMMENT '变量分组',
    default_value VARCHAR(500) COMMENT '默认值',
    is_required TINYINT DEFAULT 0 COMMENT '是否必填：0-否，1-是',
    validation_rule VARCHAR(200) COMMENT '校验规则JSON',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_template_id (template_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板变量表';

-- 5. 数据源表
CREATE TABLE sys_template_datasource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '数据源名称',
    type VARCHAR(20) NOT NULL COMMENT '类型：api/sql/static',
    url VARCHAR(500) COMMENT 'API地址/数据库连接串',
    method VARCHAR(10) DEFAULT 'GET' COMMENT '请求方式：GET/POST',
    headers TEXT COMMENT 'API请求头JSON',
    params TEXT COMMENT 'API请求参数/SQL参数JSON',
    sql_content TEXT COMMENT 'SQL语句内容',
    data_path VARCHAR(100) DEFAULT '$' COMMENT '数据解析路径',
    static_data TEXT COMMENT '静态数据JSON',
    timeout INT DEFAULT 30000 COMMENT '超时时间（毫秒）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用',
    create_by VARCHAR(50) COMMENT '创建人标识',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人标识',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_type_status (type, status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源表';

-- 6. 打印任务表
CREATE TABLE sys_print_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_no VARCHAR(50) NOT NULL COMMENT '任务编号',
    template_id BIGINT COMMENT '模板ID',
    template_name VARCHAR(100) COMMENT '模板名称',
    data_content LONGTEXT COMMENT '打印数据JSON',
    printer_name VARCHAR(100) COMMENT '打印机名称',
    paper_size VARCHAR(50) COMMENT '纸张大小',
    orientation VARCHAR(20) DEFAULT 'portrait' COMMENT '方向：portrait/landscape',
    copies INT DEFAULT 1 COMMENT '打印份数',
    page_ranges VARCHAR(100) COMMENT '页码范围',
    duplex TINYINT DEFAULT 0 COMMENT '单双面：0-单面，1-双面长边，2-双面短边',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待打印，1-打印中，2-已完成，3-失败',
    fail_reason VARCHAR(500) COMMENT '失败原因',
    print_time DATETIME COMMENT '打印完成时间',
    create_by VARCHAR(50) COMMENT '创建人标识',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE INDEX uk_task_no (task_no),
    INDEX idx_template_id (template_id),
    INDEX idx_status_create (status, create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打印任务表';

-- 7. 操作日志表
CREATE TABLE sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    module VARCHAR(50) COMMENT '模块：template/datasource/print',
    operation VARCHAR(50) COMMENT '操作类型：create/update/delete/print',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id BIGINT COMMENT '资源ID',
    params TEXT COMMENT '操作参数JSON',
    result VARCHAR(20) DEFAULT 'success' COMMENT '操作结果：success/fail',
    error_message VARCHAR(500) COMMENT '错误信息',
    ip VARCHAR(50) COMMENT '操作IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    create_by VARCHAR(50) COMMENT '操作人标识',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_module_resource (module, resource_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 初始化分类数据
INSERT INTO sys_template_category (id, name, parent_id, sort, status) VALUES
(1, '业务表单', 0, 1, 1),
(2, '商品标签', 0, 2, 1),
(3, '票据单据', 0, 3, 1),
(4, '快递面单', 0, 4, 1),
(5, '证件卡片', 0, 5, 1);
