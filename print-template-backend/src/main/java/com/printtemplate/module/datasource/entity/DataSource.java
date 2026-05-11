package com.printtemplate.module.datasource.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_template_datasource")
public class DataSource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String url;

    private String method;

    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String headers;

    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String params;

    private String sqlContent;

    private String dataPath;

    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String staticData;

    private Integer timeout;

    private Integer status;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
