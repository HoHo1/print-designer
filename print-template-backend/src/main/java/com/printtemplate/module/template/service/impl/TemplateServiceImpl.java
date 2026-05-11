package com.printtemplate.module.template.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.printtemplate.common.exception.BusinessException;
import com.printtemplate.module.template.entity.Template;
import com.printtemplate.module.template.mapper.TemplateMapper;
import com.printtemplate.module.template.service.TemplateService;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {

    @Override
    public IPage<Template> pageQuery(Integer pageNum, Integer pageSize, String keyword, Long categoryId) {
        LambdaQueryWrapper<Template> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Template::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Template::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Template::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Template createTemplate(Template template) {
        template.setCode("TPL_" + System.currentTimeMillis());
        template.setVersion(1);
        template.setStatus(1);
        template.setIsDefault(0);
        template.setUseCount(0);
        this.save(template);
        return template;
    }

    @Override
    public Template updateTemplate(Template template) {
        Template oldTemplate = this.getById(template.getId());
        if (oldTemplate == null) {
            throw new BusinessException("模板不存在");
        }
        template.setVersion(oldTemplate.getVersion() + 1);
        this.updateById(template);
        return template;
    }

    @Override
    public Template copyTemplate(Long id) {
        Template source = this.getById(id);
        if (source == null) {
            throw new BusinessException("模板不存在");
        }
        Template copy = new Template();
        BeanUtils.copyProperties(source, copy);
        copy.setId(null);
        copy.setName(source.getName() + "_副本");
        copy.setCode("TPL_" + System.currentTimeMillis());
        copy.setVersion(1);
        copy.setUseCount(0);
        this.save(copy);
        return copy;
    }

    @Override
    public void deleteTemplate(Long id) {
        this.removeById(id);
    }

    @Override
    public String exportTemplate(Long id) {
        Template template = this.getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        return template.getContent();
    }
}
