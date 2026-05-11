package com.printtemplate.module.print.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.printtemplate.module.print.entity.PrintTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PrintTaskMapper extends BaseMapper<PrintTask> {
}
