package com.printtemplate.module.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_template")
public class Template {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private Long categoryId;

    private String thumbnail;

    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String content;

    private Integer version;

    private Integer status;

    private Integer isDefault;

    private Integer useCount;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String remark;

    @TableLogic
    private Integer deleted;
}
