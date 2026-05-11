package com.printtemplate.module.datasource.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.printtemplate.common.exception.BusinessException;
import com.printtemplate.module.datasource.entity.DataSource;
import com.printtemplate.module.datasource.mapper.DataSourceMapper;
import com.printtemplate.module.datasource.service.DataSourceService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {

    @Override
    public Object previewData(Long id, Object params) {
        DataSource ds = this.getById(id);
        if (ds == null) throw new BusinessException("数据源不存在");

        try {
            switch (ds.getType()) {
                case "static":
                    return JSONUtil.parse(ds.getStaticData());
                case "api":
                    return fetchApiData(ds, params);
                case "sql":
                    // SQL执行实现，实际项目中需要JDBC连接
                    return "SQL执行预览（需配置数据库连接）";
                default:
                    return "Unsupported type";
            }
        } catch (Exception e) {
            throw new BusinessException("数据获取失败:" + e.getMessage());
        }
    }

    @Override
    public boolean testConnection(Long id) {
        try {
            previewData(id, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Object fetchApiData(DataSource ds, Object params) {
        Map<String, String> headers = new HashMap<>();
        if (ds.getHeaders() != null && !ds.getHeaders().isEmpty()) {
            headers = JSONUtil.toBean(ds.getHeaders(), Map.class);
        }

        HttpRequest request = HttpRequest.get(ds.getUrl());
        if ("POST".equalsIgnoreCase(ds.getMethod())) {
            request = HttpRequest.post(ds.getUrl()).body(JSONUtil.toJsonStr(params));
        }

        headers.forEach(request::header);

        HttpResponse response = request.execute();
        return JSONUtil.parse(response.body());
    }
}
