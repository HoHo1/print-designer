package com.printtemplate.module.print.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_print_task")
public class PrintTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskNo;

    private Long templateId;

    private String templateName;

    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String dataContent;

    private String printerName;

    private String paperSize;

    private String orientation;

    private Integer copies;

    private Integer duplex;

    private Integer status;

    private String failReason;

    private LocalDateTime printTime;

    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
