package com.catmmao.wechatshop.api.data;

import java.util.List;

import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;

public class RpcOrderResponse {
    Order order;
    List<OrderGoodsMapping> goodsList;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderGoodsMapping> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<OrderGoodsMapping> goodsList) {
        this.goodsList = goodsList;
    }
}
