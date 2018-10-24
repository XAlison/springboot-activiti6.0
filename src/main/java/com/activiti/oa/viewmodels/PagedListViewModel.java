package com.activiti.oa.viewmodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagedListViewModel<T> {
    private List<T> rows;
    private Map<String, String> titles;
    private Long total;

    public PagedListViewModel(List<T> data, Long total) {
        this.rows = (data != null) ? data : new ArrayList<>();
        this.total = total;
    }

    public PagedListViewModel(List<T> data, Long total, Map<String, String> titles) {
        this.rows = (data != null) ? data : new ArrayList<>();
        this.titles = (titles != null) ? titles : new HashMap<>();
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public Long getTotal() {
        return total;
    }

    public Map<String, String> getTitles() {
        return titles;
    }
}
