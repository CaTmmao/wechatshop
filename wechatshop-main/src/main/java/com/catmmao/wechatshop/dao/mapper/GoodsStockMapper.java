package com.catmmao.wechatshop.dao.mapper;

import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsStockMapper {
    /**
     * 扣减商品库存
     * @param goodsOnlyContainGoodsIdAndNumber 包含商品ID和数量的对象
     * @return 在数据库中扣减成功的行数
     */
    int deductStock(GoodsOnlyContainGoodsIdAndNumber goodsOnlyContainGoodsIdAndNumber);
}
