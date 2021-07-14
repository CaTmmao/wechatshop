package com.catmmao.wechatshop.model.response;

import java.util.List;

import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.model.GoodsWithNumber;
import com.catmmao.wechatshop.generated.Shop;

public class OrderResponse extends Order {
    private Shop shop;
    private List<GoodsWithNumber> goods;

    public OrderResponse() {
    }

    public OrderResponse(Order order) {
        this.setAddress(order.getAddress());
        this.setTotalPrice(order.getTotalPrice());
        this.setExpressCompany(order.getExpressCompany());
        this.setExpressId(order.getExpressId());
        this.setId(order.getId());
        this.setStatus(order.getStatus());
        this.setUserId(order.getUserId());
        this.setCreatedAt(order.getCreatedAt());
        this.setUpdatedAt(order.getUpdatedAt());
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<GoodsWithNumber> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsWithNumber> goods) {
        this.goods = goods;
    }
}
