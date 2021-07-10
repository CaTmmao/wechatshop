package com.catmmao.wechatshop.controller;

import com.catmmao.wechatshop.api.rpc.OrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    @DubboReference(version = "${wechatshop.order.version}")
    private OrderService orderService;
}
