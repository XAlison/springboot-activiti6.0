package com.activiti.oa.viewmodels;

import java.util.HashMap;
import java.util.Map;

public class PagedFilterViewModel {
    private Integer start;
    private Integer page;
    private Integer size;
    private String sort;
    private String order;
    private Map<String, String> filters;
    private Map<String, String> params;

    public PagedFilterViewModel() {
        filters = new HashMap<>();
    }

    public Integer getStart() {
        return (start != null) ? start : 0;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getPage() {
        return (page != null) ? page : 1;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return (size != null) ? size : 10;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getEnd() {
        return getStart() + getSize();
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }

    public boolean containsKey(String key) {
        return filters.containsKey(key) && !filters.get(key).isEmpty();
    }

    public void put(String key, String value) {
        filters.put(key, value);
    }

    public String get(String key) {
        return filters.get(key);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
