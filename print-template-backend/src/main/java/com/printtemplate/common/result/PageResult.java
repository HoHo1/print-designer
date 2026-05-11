package com.printtemplate.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long total;
    private Long page;
    private Long pageSize;
    private Long totalPages;
    private List<T> records;

    public PageResult(Long total, Long page, Long pageSize, List<T> records) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (total + pageSize - 1) / pageSize;
        this.records = records;
    }

    public static <T> PageResult<T> of(Long total, Long page, Long pageSize, List<T> records) {
        return new PageResult<>(total, page, pageSize, records);
    }

    public static <T, E> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<E> page, List<T> records) {
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }
}
