package com.printtemplate.module.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.printtemplate.module.datasource.entity.DataSource;

public interface DataSourceService extends IService<DataSource> {

    Object previewData(Long id, Object params);

    boolean testConnection(Long id);
}
