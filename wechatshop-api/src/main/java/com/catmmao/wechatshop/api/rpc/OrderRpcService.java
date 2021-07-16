package com.catmmao.wechatshop.api.rpc;

import java.util.List;

import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import com.catmmao.wechatshop.api.data.RpcOrderResponse;
import com.catmmao.wechatshop.api.generated.Order;

public interface OrderRpcService {
    Order createOrder(List<GoodsOnlyContainGoodsIdAndNumber> goodsOnlyContainGoodsIdAndNumberList, Order order);
    RpcOrderResponse deleteOrderByOrderId(long orderId, long userId);
}