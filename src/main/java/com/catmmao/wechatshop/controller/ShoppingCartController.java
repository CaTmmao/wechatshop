package com.catmmao.wechatshop.controller;

import com.catmmao.wechatshop.model.response.PaginationResponseModel;
import com.catmmao.wechatshop.model.response.ShoppingCartResponseModel;
import com.catmmao.wechatshop.service.ShoppingCartService;
import org.springframework.web.bind.annotation.GetMapping;
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
     * 获取当前用户名下的所有购物车物品
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @return 用户名下的所有购物车物品
     */
    @GetMapping
    public PaginationResponseModel<ShoppingCartResponseModel> getUserShoppingCartListByUserId(
        @RequestParam int pageNum,
        @RequestParam int pageSize) {
        return shoppingCartService.getUserShoppingCart(pageNum, pageSize);
    }
}
