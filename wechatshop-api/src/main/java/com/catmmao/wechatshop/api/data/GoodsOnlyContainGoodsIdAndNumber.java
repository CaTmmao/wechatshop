package com.catmmao.wechatshop.api.data;

import java.io.Serializable;

public class GoodsOnlyContainGoodsIdAndNumber implements Serializable {
    private long goodsId;
    private int number;

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
