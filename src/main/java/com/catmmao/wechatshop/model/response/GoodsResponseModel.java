package com.catmmao.wechatshop.model.response;

import com.catmmao.wechatshop.model.generated.Goods;

public class GoodsResponseModel {
    private Goods data;

    public GoodsResponseModel(Goods data) {
        this.data = data;
    }

    public Goods getData() {
        return data;
    }

    public void setData(Goods data) {
        this.data = data;
    }
}
