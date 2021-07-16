package com.catmmao.wechatshop.controller;

import java.util.Optional;

import com.catmmao.wechatshop.api.data.OrderInfo;
import com.catmmao.wechatshop.model.response.OrderResponse;
import com.catmmao.wechatshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable("id") long orderId) {
        return ResponseEntity.of(Optional.of(orderService.deleteOrder(orderId)));
    }
}
