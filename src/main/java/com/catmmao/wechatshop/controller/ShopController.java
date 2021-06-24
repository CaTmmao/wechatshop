package com.catmmao.wechatshop.controller;

import java.util.Optional;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.model.generated.Shop;
import com.catmmao.wechatshop.model.response.CommonResponseModel;
import com.catmmao.wechatshop.model.response.PaginationResponseModel;
import com.catmmao.wechatshop.service.ShopService;
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
@RequestMapping("/api/v1/shop")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    public void sanitize(Shop shop) {
        shop.setId(null);
        shop.setCreatedAt(null);
        shop.setUpdatedAt(null);
    }

    /**
     * 创建店铺
     *
     * @param shop 店铺信息
     * @return 创建好的店铺信息
     */
    @PostMapping
    public ResponseEntity<CommonResponseModel<Shop>> createShop(@RequestBody Shop shop) {
        sanitize(shop);
        long creatorId = UserContext.getCurrentUser().getId();
        Shop result = shopService.createShop(shop, creatorId);
        CommonResponseModel<Shop> responseBody = CommonResponseModel.of(result);
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    /**
     * 修改店铺
     *
     * @param shopId 店铺ID
     * @param shop   店铺信息
     * @return 修改后的店铺信息
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponseModel<Shop>> updateGoods(@PathVariable("id") Long shopId,
                                                                 @RequestBody Shop shop) {
        sanitize(shop);
        Long userId = UserContext.getCurrentUser().getId();
        shopService.updateShop(shop, userId);
        CommonResponseModel<Shop> responseBody = CommonResponseModel.of(shop);
        return ResponseEntity.of(Optional.of(responseBody));
    }

    /**
     * 删除店铺
     *
     * @param shopId 店铺ID
     * @return 删除成功的店铺信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseModel<Shop>> deleteShop(@PathVariable("id") Long shopId) {
        Long userId = UserContext.getCurrentUser().getId();
        Shop result = shopService.deleteShop(shopId, userId);
        CommonResponseModel<Shop> responseBody = CommonResponseModel.of(result);
        return new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);
    }

    /**
     * 获取当前用户名下的所有店铺
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @return 获取到的店铺列表
     */
    @GetMapping
    public ResponseEntity<PaginationResponseModel<Shop>> getMyShopList(@RequestParam int pageNum,
                                                                       @RequestParam int pageSize) {
        PaginationResponseModel<Shop> responseBody;
        Long userId = UserContext.getCurrentUser().getId();
        responseBody = shopService.getMyShopListByUserId(userId, pageNum, pageSize);
        return ResponseEntity.of(Optional.of(responseBody));
    }

    /**
     * 获取指定ID的店铺
     *
     * @param shopId 店铺ID
     * @return 获取到的店铺信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseModel<Shop>> getShopByShopId(@PathVariable("id") Long shopId) {
        Shop result = shopService.getShopByShopId(shopId);
        CommonResponseModel<Shop> responseBody = CommonResponseModel.of(result);
        return ResponseEntity.of(Optional.of(responseBody));
    }
}
