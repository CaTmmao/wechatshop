package com.catmmao.wechatshop.controller;

import java.util.Date;

import com.catmmao.wechatshop.model.generated.Goods;
import com.catmmao.wechatshop.model.response.GoodsResponseModel;
import com.catmmao.wechatshop.service.GoodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("/goods")
    public ResponseEntity<GoodsResponseModel> createGoods(@RequestBody Goods goods) {
        sanitize(goods);
        goods = goodsService.createGoods(goods);
        GoodsResponseModel data = new GoodsResponseModel(goods);
        return new ResponseEntity<>(data, HttpStatus.CREATED);
    }

    /**
     * 删除不需要的数据（仅针对创建商品接口）
     *
     * @param goods 商品对象
     */
    public void sanitize(Goods goods) {
        goods.setId(null);
        goods.setCreatedAt(new Date());
        goods.setUpdatedAt(null);
    }
}
