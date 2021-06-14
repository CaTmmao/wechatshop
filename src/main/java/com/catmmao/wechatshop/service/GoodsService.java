package com.catmmao.wechatshop.service;

import com.catmmao.wechatshop.dao.GoodsDao;
import com.catmmao.wechatshop.model.generated.Goods;
import org.springframework.stereotype.Service;

@Service
public class GoodsService {
    private final GoodsDao goodsDao;

    public GoodsService(GoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }

    public Goods createGoods(Goods goods) {
        return goodsDao.insert(goods);
    }
}
