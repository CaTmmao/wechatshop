package com.catmmao.wechatshop.model.response;

public class PaginationResponseModel<T> {
    // 每页显示的数量
    private Integer pageSize;
    // 当前页数，从 1 开始
    private Integer pageNum;
    // 共有多少页
    private Integer totalPage;

    // 列表
    private T data;

    public PaginationResponseModel(Integer pageSize, Integer pageNum, Integer totalPage, T data) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.totalPage = totalPage;
        this.data = data;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
