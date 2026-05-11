package com.printtemplate.module.print.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.printtemplate.common.exception.BusinessException;
import com.printtemplate.module.print.entity.PrintTask;
import com.printtemplate.module.print.mapper.PrintTaskMapper;
import com.printtemplate.module.print.service.PrintTaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PrintTaskServiceImpl extends ServiceImpl<PrintTaskMapper, PrintTask> implements PrintTaskService {

    @Override
    public IPage<PrintTask> pageQuery(Integer pageNum, Integer pageSize, Integer status) {
        LambdaQueryWrapper<PrintTask> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(PrintTask::getStatus, status);
        }
        wrapper.orderByDesc(PrintTask::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public PrintTask createTask(PrintTask task) {
        task.setTaskNo("PT" + System.currentTimeMillis());
        task.setStatus(0);
        task.setCopies(task.getCopies() == null ? 1 : task.getCopies());
        task.setDuplex(task.getDuplex() == null ? 0 : task.getDuplex());
        this.save(task);
        return task;
    }

    @Override
    public PrintTask getTaskById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean updateTaskStatus(Long id, Integer status, String failReason) {
        PrintTask task = this.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        task.setStatus(status);
        if (status == 2 && failReason != null) {
            task.setFailReason(failReason);
        }
        if (status == 1) {
            task.setPrintTime(LocalDateTime.now());
        }
        return this.updateById(task);
    }

    @Override
    public boolean deleteTask(Long id) {
        return this.removeById(id);
    }
}
