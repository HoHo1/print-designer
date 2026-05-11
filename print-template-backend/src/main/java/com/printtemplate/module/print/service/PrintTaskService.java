package com.printtemplate.module.print.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.printtemplate.module.print.entity.PrintTask;

public interface PrintTaskService {
    IPage<PrintTask> pageQuery(Integer pageNum, Integer pageSize, Integer status);
    PrintTask createTask(PrintTask task);
    PrintTask getTaskById(Long id);
    boolean updateTaskStatus(Long id, Integer status, String failReason);
    boolean deleteTask(Long id);
}
