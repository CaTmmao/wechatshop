package com.catmmao.wechatshop.model;

import com.catmmao.wechatshop.generated.Goods;

public class GoodsWithNumber extends Goods {
    // 商品数量
    private int number;

    public GoodsWithNumber() {
    }

    public GoodsWithNumber(Goods goods) {
        this.setId(goods.getId());
        this.setShopId(goods.getShopId());
        this.setName(goods.getName());
        this.setDescription(goods.getDescription());
        this.setDetails(goods.getDetails());
        this.setImgUrl(goods.getImgUrl());
        this.setPrice(goods.getPrice());
        this.setStock(goods.getStock());
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
