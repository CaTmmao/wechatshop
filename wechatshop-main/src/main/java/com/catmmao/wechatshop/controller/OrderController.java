package com.catmmao.wechatshop.controller;

import com.catmmao.wechatshop.api.data.OrderInfo;
import com.catmmao.wechatshop.model.response.OrderResponse;
import com.catmmao.wechatshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderInfo orderInfo) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(orderService.createOrder(orderInfo));
    }
}
