package com.camel.clinic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiPaged<T> {
    @JsonProperty("data")
    private List<T> data;

    @JsonProperty("total")
    private long total;

    @JsonProperty("page")
    private int page;

    @JsonProperty("size")
    private int size;

    @JsonProperty("totalPages")
    private int totalPages;

    public ApiPaged() {
    }

    public ApiPaged(List<T> data, long total, int page, int size, int totalPages) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
    }

    public static <T> ApiPaged<T> of(List<T> items, long total, int page, int size, int totalPages) {
        return new ApiPaged<>(items, total, page, size, totalPages);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

