package com.catmmao.wechatshop.model.response;

public class CommonResponse<T> {
    private String message;
    private T data;

    public CommonResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> of(T data) {
        return new CommonResponse<>(null, data);
    }

    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(message, null);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
