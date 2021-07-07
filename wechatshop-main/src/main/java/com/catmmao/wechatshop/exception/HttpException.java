package com.catmmao.wechatshop.exception;

import org.springframework.http.HttpStatus;

/**
 * http 请求过程中遇到的如找不到资源，无权限等错误
 */
public class HttpException extends RuntimeException {
    // 错误信息
    private String message;
    // 状态码
    private HttpStatus httpStatus;

    private HttpException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public static HttpException resourceNotFound(String message) {
        return new HttpException(message, HttpStatus.NOT_FOUND);
    }

    public static HttpException badRequest(String message) {
        return new HttpException(message, HttpStatus.BAD_REQUEST);
    }

    public static HttpException forbidden(String message) {
        return new HttpException(message, HttpStatus.FORBIDDEN);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
