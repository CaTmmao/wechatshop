package com.catmmao.wechatshop.controller;

import java.util.Date;
import java.util.List;

import com.catmmao.wechatshop.exception.ForbiddenForShopException;
import com.catmmao.wechatshop.exception.ResourceNotFoundException;
import com.catmmao.wechatshop.model.generated.Goods;
import com.catmmao.wechatshop.model.response.GoodsResponseModel;
import com.catmmao.wechatshop.model.response.PaginationResponseModel;
import com.catmmao.wechatshop.service.GoodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    /**
     * 创建商品
     *
     * @param goods 商品信息
     * @return 创建好的商品信息
     */
    @PostMapping("/goods")
    public ResponseEntity<GoodsResponseModel> createGoods(@RequestBody Goods goods) {
        sanitize(goods);
        try {
            goods = goodsService.createGoods(goods);
            GoodsResponseModel responseBody = GoodsResponseModel.of(goods);
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (ForbiddenForShopException e) {
            GoodsResponseModel responseBody = GoodsResponseModel.error(e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

    /**
     * 删除商品
     *
     * @param goodsId 商品ID
     * @return 被删除的商品信息
     */
    @DeleteMapping("/goods/{id}")
    public ResponseEntity<GoodsResponseModel> deleteGoods(@PathVariable("id") Long goodsId) {
        GoodsResponseModel responseBody;

        try {
            Goods goods = goodsService.deleteGoodsByGoodsId(goodsId);
            responseBody = GoodsResponseModel.of(goods);
            return new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);
        } catch (ForbiddenForShopException e) {
            responseBody = GoodsResponseModel.error(e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        } catch (ResourceNotFoundException e) {
            responseBody = GoodsResponseModel.error(e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 获取所有商品
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @param shopId   店铺ID，若传递，则只显示该店铺中的商品
     * @return 查询到的商品列表信息
     */
    @GetMapping("/goods")
    public ResponseEntity<PaginationResponseModel<List<Goods>>> getGoodsListByShopId(@RequestParam Integer pageNum,
                                                                                     @RequestParam Integer pageSize,
                                                                                     @RequestParam(required = false)
                                                                                         Integer shopId) {
        PaginationResponseModel<List<Goods>> responseBody = goodsService.getGoodsByShopId(pageNum, pageSize, shopId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
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
