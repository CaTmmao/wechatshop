package com.catmmao.wechatshop.model.response;

public class CommonResponseModel<T> {
    private String message;
    private T data;

    public CommonResponseModel(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponseModel<T> of(T data) {
        return new CommonResponseModel<>(null, data);
    }

    public static <T> CommonResponseModel<T> error(String message) {
        return new CommonResponseModel<>(message, null);
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
