package com.printtemplate.module.datasource.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.printtemplate.common.result.PageResult;
import com.printtemplate.common.result.Result;
import com.printtemplate.module.datasource.entity.DataSource;
import com.printtemplate.module.datasource.service.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "数据源管理", description = "数据源CRUD接口")
@RestController
@RequestMapping("/datasource")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @Operation(summary = "分页查询数据源列表")
    @GetMapping("/list")
    public Result<PageResult<DataSource>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type
    ) {
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(DataSource::getType, type);
        }
        wrapper.orderByDesc(DataSource::getCreateTime);
        IPage<DataSource> page = dataSourceService.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<DataSource> result = PageResult.of(page.getTotal(), pageNum, pageSize, page.getRecords());
        return Result.success(result);
    }

    @Operation(summary = "获取数据源详情")
    @GetMapping("/{id}")
    public Result<DataSource> getById(@PathVariable Long id) {
        return Result.success(dataSourceService.getById(id));
    }

    @Operation(summary = "创建数据源")
    @PostMapping
    public Result<DataSource> create(@RequestBody DataSource dataSource) {
        dataSourceService.save(dataSource);
        return Result.success(dataSource);
    }

    @Operation(summary = "更新数据源")
    @PutMapping
    public Result<DataSource> update(@RequestBody DataSource dataSource) {
        dataSourceService.updateById(dataSource);
        return Result.success(dataSource);
    }

    @Operation(summary = "删除数据源")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dataSourceService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "测试数据源连接")
    @PostMapping("/{id}/test")
    public Result<Boolean> test(@PathVariable Long id) {
        return Result.success(dataSourceService.testConnection(id));
    }

    @Operation(summary = "预览数据源数据")
    @PostMapping("/{id}/preview")
    public Result<Object> preview(@PathVariable Long id, @RequestBody(required = false) Object params) {
        return Result.success(dataSourceService.previewData(id, params));
    }
}
