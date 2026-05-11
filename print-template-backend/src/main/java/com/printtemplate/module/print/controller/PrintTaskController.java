package com.printtemplate.module.print.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.printtemplate.common.result.PageResult;
import com.printtemplate.common.result.Result;
import com.printtemplate.module.print.entity.PrintTask;
import com.printtemplate.module.print.service.PrintTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "打印任务管理", description = "打印任务CRUD接口")
@RestController
@RequestMapping("/print-task")
public class PrintTaskController {

    @Autowired
    private PrintTaskService printTaskService;

    @Operation(summary = "分页查询打印任务列表")
    @GetMapping("/list")
    public Result<PageResult<PrintTask>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status
    ) {
        IPage<PrintTask> page = printTaskService.pageQuery(pageNum, pageSize, status);
        PageResult<PrintTask> result = PageResult.of(page.getTotal(), pageNum, pageSize, page.getRecords());
        return Result.success(result);
    }

    @Operation(summary = "获取打印任务详情")
    @GetMapping("/{id}")
    public Result<PrintTask> getById(@PathVariable Long id) {
        return Result.success(printTaskService.getTaskById(id));
    }

    @Operation(summary = "创建打印任务")
    @PostMapping
    public Result<PrintTask> create(@RequestBody PrintTask task) {
        return Result.success(printTaskService.createTask(task));
    }

    @Operation(summary = "更新打印任务状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String failReason
    ) {
        return Result.success(printTaskService.updateTaskStatus(id, status, failReason));
    }

    @Operation(summary = "删除打印任务")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        printTaskService.deleteTask(id);
        return Result.success();
    }
}
