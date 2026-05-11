package com.printtemplate.module.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_template_version")
public class TemplateVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long templateId;

    private Integer version;

    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String content;

    private String changeLog;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
