package com.printtemplate.module.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.printtemplate.module.template.entity.Template;

public interface TemplateService extends IService<Template> {

    IPage<Template> pageQuery(Integer pageNum, Integer pageSize, String keyword, Long categoryId);

    Template createTemplate(Template template);

    Template updateTemplate(Template template);

    Template copyTemplate(Long id);

    void deleteTemplate(Long id);

    String exportTemplate(Long id);
}
