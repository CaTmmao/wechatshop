package com.catmmao.wechatshop.dao.mapper;

import java.util.List;

import com.catmmao.wechatshop.model.response.ShoppingCartResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShoppingCartQueryMapper {
    /**
     * 计算用户购物车内包含的店铺数量
     *
     * @param userId 用户ID
     * @return 店铺数量
     */
    int countHowManyShopsInUserShoppingCart(long userId);

    /**
     * 获取用户购物车中的所有商品信息列表
     *
     * @param userId 用户ID
     * @param offset offset
     * @param limit  limit
     * @return 购物车信息
     */
    List<ShoppingCartResponse> selectShoppingCartDataListByUserId(@Param("userId") long userId,
                                                                  @Param("offset") int offset,
                                                                  @Param("limit") int limit);

    /**
     * 获取当前用户购物车中特定店铺的商品信息
     *
     * @param userId 用户ID
     * @param shopId 店铺ID
     * @return 指定店铺的所有商品信息
     */
    List<ShoppingCartResponse> selectShoppingCartDataByUserIdAndShopId(@Param("userId") long userId,
                                                                       @Param("shopId") long shopId);

    /**
     * 删除购物车表中指定商品ID的数据
     *
     * @param userId  用户ID
     * @param goodsId 商品ID
     */
    void deleteShoppingCartByUserIdAndGoodsId(@Param("userId") long userId,
                            @Param("goodsId") long goodsId);
}
