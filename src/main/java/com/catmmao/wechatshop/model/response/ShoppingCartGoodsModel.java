package com.catmmao.wechatshop.model.response;

import com.catmmao.wechatshop.model.generated.Goods;

public class ShoppingCartGoodsModel extends Goods {
    // 商品数量
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
