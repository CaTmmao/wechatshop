package com.catmmao.wechatshop.api.rpc;

import java.util.List;

import com.catmmao.wechatshop.api.data.GoodsIdAndNumber;
import com.catmmao.wechatshop.api.data.PaginationResponse;
import com.catmmao.wechatshop.api.data.RpcOrderResponse;
import com.catmmao.wechatshop.api.generated.Order;

public interface OrderRpcService {
    Order createOrder(List<GoodsIdAndNumber> listOfGoodsIdAndNumber, Order order);

    RpcOrderResponse deleteOrderByOrderId(long orderId, long userId);

    PaginationResponse<RpcOrderResponse> getAllOrders(long userId, int pageNum, int pageSize, String status);

    Order getOrderByOrderId(long orderId);

    RpcOrderResponse updateOrder(Order newOrder);
}