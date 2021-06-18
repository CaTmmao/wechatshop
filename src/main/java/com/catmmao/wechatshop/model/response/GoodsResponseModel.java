package com.catmmao.wechatshop.model.response;

import com.catmmao.wechatshop.model.generated.Goods;

public class GoodsResponseModel {
    private String message;
    private Goods data;

    public GoodsResponseModel(String message, Goods data) {
        this.message = message;
        this.data = data;
    }

    public static GoodsResponseModel createdSuccess(Goods goods) {
        return new GoodsResponseModel(null, goods);
    }

    public static GoodsResponseModel deleteSuccess(Goods goods) {
        return new GoodsResponseModel(null, goods);
    }

    public static GoodsResponseModel forbiddenForShop(String message) {
        return new GoodsResponseModel(message, null);
    }

    public static GoodsResponseModel notFound(String message) {
        return new GoodsResponseModel(message, null);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Goods getData() {
        return data;
    }

    public void setData(Goods data) {
        this.data = data;
    }
}
