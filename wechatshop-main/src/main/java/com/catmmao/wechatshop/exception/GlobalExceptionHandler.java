package com.catmmao.wechatshop.exception;

import com.catmmao.wechatshop.api.exception.HttpException;
import com.catmmao.wechatshop.model.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 拦截全局请求异常，不用在每个 controller 中单独处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpException.class)
    ResponseEntity<CommonResponse<?>> handleHttpException(HttpException e) {
        CommonResponse<?> responseBody = CommonResponse.error(e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(responseBody);
    }
}
