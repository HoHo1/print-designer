package com.printtemplate.module.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.printtemplate.module.datasource.entity.DataSource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataSourceMapper extends BaseMapper<DataSource> {
}
