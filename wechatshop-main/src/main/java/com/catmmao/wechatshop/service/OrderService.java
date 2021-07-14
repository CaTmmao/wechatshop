package com.catmmao.wechatshop.service;

import com.catmmao.wechatshop.api.data.OrderInfo;
import com.catmmao.wechatshop.model.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderInfo orderInfo);
}
