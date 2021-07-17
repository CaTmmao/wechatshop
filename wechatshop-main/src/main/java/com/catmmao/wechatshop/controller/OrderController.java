package com.catmmao.wechatshop.controller;

import java.util.Optional;

import com.catmmao.wechatshop.api.data.DbDataStatus;
import com.catmmao.wechatshop.api.data.OrderInfo;
import com.catmmao.wechatshop.api.exception.HttpException;
import com.catmmao.wechatshop.model.response.OrderResponse;
import com.catmmao.wechatshop.api.data.PaginationResponse;
import com.catmmao.wechatshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 获取当前用户名下的所有订单
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @param status   订单状态：pending 待付款 paid 已付款 delivered 物流中 received 已收货
     * @return 订单列表
     */
    @GetMapping
    public PaginationResponse<OrderResponse> getAllOrders(
        @RequestParam int pageNum,
        @RequestParam int pageSize,
        @RequestParam(required = false) String status) {
        if (status != null && !DbDataStatus.ifEnumExist(status)) {
            throw HttpException.badRequest("status 有误: " + status);
        }

        return orderService.getAllOrders(pageNum, pageSize, status);
    }
}