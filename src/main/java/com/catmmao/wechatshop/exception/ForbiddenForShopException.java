package com.catmmao.wechatshop.exception;

public class ForbiddenForShopException extends RuntimeException {
    private String message;

    public ForbiddenForShopException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
