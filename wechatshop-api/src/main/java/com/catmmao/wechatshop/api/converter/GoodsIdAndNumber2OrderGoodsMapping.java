package com.catmmao.wechatshop.api.converter;

import com.catmmao.wechatshop.api.data.GoodsIdAndNumber;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;
import com.google.common.base.Converter;
import org.springframework.stereotype.Service;

@Service
public class GoodsIdAndNumber2OrderGoodsMapping
    extends Converter<GoodsIdAndNumber, OrderGoodsMapping> {
    @Override
    protected OrderGoodsMapping doForward(GoodsIdAndNumber obj) {
        OrderGoodsMapping result = new OrderGoodsMapping();
        result.setGoodsId(obj.getId());
        result.setNumber((long) obj.getNumber());
        return result;
    }

    @Override
    protected GoodsIdAndNumber doBackward(OrderGoodsMapping obj) {
        GoodsIdAndNumber result = new GoodsIdAndNumber();
        result.setId(obj.getGoodsId());
        result.setNumber(obj.getNumber().intValue());
        return result;
    }
}
