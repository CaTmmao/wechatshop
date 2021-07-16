package com.catmmao.wechatshop.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import com.catmmao.wechatshop.api.data.RpcOrderResponse;
import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;
import com.catmmao.wechatshop.api.rpc.OrderRpcService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 模拟 order 模块的服务
 */
@DubboService(version = "${wechatshop.order.version}")
public class MockOrderRpcService implements OrderRpcService {
    @Override
    public Order createOrder(List<GoodsOnlyContainGoodsIdAndNumber> goodsOnlyContainGoodsIdAndNumberList, Order order) {
        order.setId(1L);
        return order;
    }

    @Override
    public RpcOrderResponse deleteOrderByOrderId(long orderId, long userId) {
        RpcOrderResponse result = new RpcOrderResponse();
        Order order = new Order();
        order.setId(1L);
        result.setOrder(order);

        OrderGoodsMapping goods1 = new OrderGoodsMapping();
        goods1.setGoodsId(1L);
        goods1.setNumber(4L);
        OrderGoodsMapping goods2 = new OrderGoodsMapping();
        goods2.setGoodsId(2L);
        goods2.setNumber(5L);
        List<OrderGoodsMapping> list = new ArrayList<>(Arrays.asList(goods1, goods2));
        result.setGoodsList(list);
        return result;
    }
}
