package com.catmmao.wechatshop.model;

import com.catmmao.wechatshop.generated.Goods;

public class GoodsWithNumber extends Goods {
    // 商品数量
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
