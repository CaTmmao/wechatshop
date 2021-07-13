package com.catmmao.wechatshop.model.response;

import java.util.List;

import com.catmmao.wechatshop.model.GoodsWithNumber;
import com.catmmao.wechatshop.generated.Shop;

/**
 * 购物车接口的返回值模型
 */
public class ShoppingCartResponseModel {
    // 店铺信息
    private Shop shop;
    // 商品列表信息
    private List<GoodsWithNumber> goods;

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

    public void putGoods(GoodsWithNumber goods) {
        this.goods.add(goods);
    }
}
