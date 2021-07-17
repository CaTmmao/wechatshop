package com.catmmao.wechatshop.api.converter;

import java.util.ArrayList;
import java.util.List;

import com.catmmao.wechatshop.api.data.GoodsIdAndNumber;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;
import org.springframework.stereotype.Service;

@Service
public class CommonConverter {
    private final GoodsIdAndNumber2OrderGoodsMapping goodsIdAndNumber2OrderGoodsMapping;

    public CommonConverter(
        GoodsIdAndNumber2OrderGoodsMapping goodsIdAndNumber2OrderGoodsMapping) {
        this.goodsIdAndNumber2OrderGoodsMapping = goodsIdAndNumber2OrderGoodsMapping;
    }

    /**
     * 将 list OrderGoodsMapping 转换成 list GoodsIdAndNumber
     *
     * @param list list, OrderGoodsMapping
     * @return list, GoodsIdAndNumber
     */
    public List<GoodsIdAndNumber> listOfOrderGoodsMapping2GoodsIdAndNumber(
        List<OrderGoodsMapping> list) {
        List<GoodsIdAndNumber> result = new ArrayList<>();
        goodsIdAndNumber2OrderGoodsMapping.reverse().convertAll(list)
            .forEach(result::add);
        return result;
    }

    /**
     * 将 list GoodsIdAndNumber 转换成 list OrderGoodsMapping
     *
     * @param list list, GoodsIdAndNumber
     * @return list, OrderGoodsMapping
     */
    public List<OrderGoodsMapping> listOfGoodsIdAndNumber2OrderGoodsMapping(
        List<GoodsIdAndNumber> list) {
        List<OrderGoodsMapping> result = new ArrayList<>();
        goodsIdAndNumber2OrderGoodsMapping.convertAll(list)
            .forEach(result::add);
        return result;
    }
}
