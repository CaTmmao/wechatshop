package com.catmmao.wechatshop.service;

import com.catmmao.wechatshop.api.data.OrderInfo;
import com.catmmao.wechatshop.model.response.OrderResponse;
import com.catmmao.wechatshop.api.data.PaginationResponse;

public interface OrderService {
    OrderResponse createOrder(OrderInfo orderInfo);

    OrderResponse deleteOrder(long orderId);

    PaginationResponse<OrderResponse> getAllOrders(int pageNum, int pageSize, String status);
}
