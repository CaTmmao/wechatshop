package com.catmmao.wechatshop.controller;

import java.util.Optional;

import com.catmmao.wechatshop.model.response.CommonResponse;
import com.catmmao.wechatshop.api.data.PaginationResponse;
import com.catmmao.wechatshop.model.response.ShoppingCartResponse;
import com.catmmao.wechatshop.service.ShoppingCartService;
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
@RequestMapping("/api/v1/shopping_cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    /**
     * 加购物车
     *
     * @param goodsList 需要添加的商品列表
     * @return 已添加进购物车的该店铺的商品列表
     */
    @PostMapping
    public ResponseEntity<CommonResponse<ShoppingCartResponse>> addGoodsToShoppingCart(
        @RequestBody ShoppingCartResponse goodsList) {
        ShoppingCartResponse data = shoppingCartService.addGoodsToShoppingCart(goodsList);
        return ResponseEntity.of(Optional.of(CommonResponse.of(data)));
    }

    /**
     * 删除当前用户购物车中指定的商品
     *
     * @param goodsId 要删除的商品ID
     * @return 更新后的该店铺物品列表
     */
    @DeleteMapping("/{goodsId}")
    public ResponseEntity<CommonResponse<ShoppingCartResponse>> deleteGoodsInShoppingCart(
        @PathVariable long goodsId) {
        ShoppingCartResponse data = shoppingCartService.deleteGoodsInShoppingCart(goodsId);
        return ResponseEntity.of(Optional.of(CommonResponse.of(data)));
    }

    /**
     * 获取当前用户名下的所有购物车物品
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @return 用户名下的所有购物车物品
     */
    @GetMapping
    public PaginationResponse<ShoppingCartResponse> getUserShoppingCartListByUserId(
        @RequestParam int pageNum,
        @RequestParam int pageSize) {
        return shoppingCartService.getUserShoppingCart(pageNum, pageSize);
    }
}
