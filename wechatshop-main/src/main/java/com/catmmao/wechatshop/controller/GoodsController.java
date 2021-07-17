package com.catmmao.wechatshop.controller;

import java.util.Optional;
import javax.websocket.server.PathParam;

import com.catmmao.wechatshop.generated.Goods;
import com.catmmao.wechatshop.model.response.CommonResponse;
import com.catmmao.wechatshop.api.data.PaginationResponse;
import com.catmmao.wechatshop.service.GoodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goods")
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
    @PostMapping
    public ResponseEntity<CommonResponse<Goods>> createGoods(@RequestBody Goods goods) {
        sanitize(goods);
        goods = goodsService.createGoods(goods);
        CommonResponse<Goods> responseBody = CommonResponse.of(goods);
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    /**
     * 删除商品
     *
     * @param goodsId 商品ID
     * @return 被删除的商品信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Goods>> deleteGoods(@PathVariable("id") Long goodsId) {
        Goods goods = goodsService.deleteGoodsByGoodsId(goodsId);
        CommonResponse<Goods> responseBody = CommonResponse.of(goods);
        return new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);
    }

    /**
     * 获取所有商品
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @param shopId   店铺ID，若传递，则只显示该店铺中的商品
     * @return 查询到的商品列表信息
     */
    @GetMapping
    public ResponseEntity<PaginationResponse<Goods>> getGoodsListByShopId(@RequestParam Integer pageNum,
                                                                          @RequestParam Integer pageSize,
                                                                          @RequestParam(required = false)
                                                                                   Integer shopId) {
        PaginationResponse<Goods> responseBody = goodsService.getGoodsByShopId(pageNum, pageSize, shopId);
        return ResponseEntity.of(Optional.of(responseBody));
    }

    /**
     * 更新商品
     *
     * @param goodsId 商品ID
     * @param goods   商品信息
     * @return 更新后的商品信息
     */
    @PatchMapping
    public ResponseEntity<CommonResponse<Goods>> updateGoods(@PathParam("id") Long goodsId,
                                                             @RequestBody Goods goods) {
        sanitize(goods);
        goods.setId(goodsId);
        Goods result = goodsService.updateGoods(goods);
        CommonResponse<Goods> responseBody = CommonResponse.of(result);
        return ResponseEntity.of(Optional.of(responseBody));
    }

    /**
     * 获取指定ID的商品
     *
     * @param goodsId 商品ID
     * @return 商品信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Goods>> getGoodsByGoodsId(@PathVariable("id") long goodsId) {
        Goods result = goodsService.getGoodsByGoodsId(goodsId);
        CommonResponse<Goods> responseBody = CommonResponse.of(result);
        return ResponseEntity.of(Optional.of(responseBody));
    }

    /**
     * 删除不需要的数据（仅针对创建商品接口）
     *
     * @param goods 商品对象
     */
    public void sanitize(Goods goods) {
        goods.setId(null);
        goods.setCreatedAt(null);
        goods.setUpdatedAt(null);
        goods.setStatus(null);
    }
}
