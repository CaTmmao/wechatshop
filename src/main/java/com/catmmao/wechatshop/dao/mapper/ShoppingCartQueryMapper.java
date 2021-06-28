package com.catmmao.wechatshop.dao.mapper;

import java.util.List;

import com.catmmao.wechatshop.model.response.ShoppingCartResponseModel;
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
     * 获取用户购物车信息
     *
     * @param userId 用户ID
     * @param offset offset
     * @param limit  limit
     * @return 购物车信息
     */
    List<ShoppingCartResponseModel> selectShoppingCartDataByUserId(@Param("userId") long userId,
                                                                   @Param("offset") int offset,
                                                                   @Param("limit") int limit);
}
