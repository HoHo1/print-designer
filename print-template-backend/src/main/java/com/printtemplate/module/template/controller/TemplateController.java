package com.printtemplate.module.template.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.printtemplate.common.result.PageResult;
import com.printtemplate.common.result.Result;
import com.printtemplate.module.template.entity.Template;
import com.printtemplate.module.template.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "模板管理", description = "模板CRUD接口")
@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Operation(summary = "分页查询模板列表")
    @GetMapping("/list")
    public Result<PageResult<Template>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId
    ) {
        IPage<Template> page = templateService.pageQuery(pageNum, pageSize, keyword, categoryId);
        PageResult<Template> result = PageResult.of(page.getTotal(), pageNum, pageSize, page.getRecords());
        return Result.success(result);
    }

    @Operation(summary = "获取模板详情")
    @GetMapping("/{id}")
    public Result<Template> getById(@PathVariable Long id) {
        return Result.success(templateService.getById(id));
    }

    @Operation(summary = "创建模板")
    @PostMapping
    public Result<Template> create(@Valid @RequestBody Template template) {
        return Result.success(templateService.createTemplate(template));
    }

    @Operation(summary = "更新模板")
    @PutMapping
    public Result<Template> update(@Valid @RequestBody Template template) {
        return Result.success(templateService.updateTemplate(template));
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return Result.success();
    }

    @Operation(summary = "复制模板")
    @PostMapping("/{id}/copy")
    public Result<Template> copy(@PathVariable Long id) {
        return Result.success(templateService.copyTemplate(id));
    }

    @Operation(summary = "导出模板")
    @GetMapping("/{id}/export")
    public Result<String> export(@PathVariable Long id) {
        return Result.success(templateService.exportTemplate(id));
    }
}
