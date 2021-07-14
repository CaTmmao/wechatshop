package com.catmmao.wechatshop.orderService.service;

import java.util.List;

import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.generated.OrderMapper;
import com.catmmao.wechatshop.api.rpc.OrderRpcService;
import com.catmmao.wechatshop.orderService.dao.OrderGoodsDao;
import org.apache.dubbo.config.annotation.DubboService;

// 本模块的版本号，在 application.yml 中定义
@DubboService(version = "${wechatshop.order.version}")
public class RpcOrderRpcServiceIpl implements OrderRpcService {
    private final OrderMapper orderMapper;
    private final OrderGoodsDao orderGoodsDao;

    public RpcOrderRpcServiceIpl(OrderMapper orderMapper,
                                 OrderGoodsDao orderGoodsDao) {
        this.orderMapper = orderMapper;
        this.orderGoodsDao = orderGoodsDao;
    }

    @Override
    public Order createOrder(List<GoodsOnlyContainGoodsIdAndNumber> goodsOnlyContainGoodsIdAndNumberList,
                             Order order) {
        order = orderGoodsDao.insertOrder(order);
        orderGoodsDao.insertMultiOrderGoods(goodsOnlyContainGoodsIdAndNumberList, order.getId());

        return order;
    }

    public Order insertOrder(Order order) {
        Long totalPrice = order.getTotalPrice();

        if (totalPrice == null || totalPrice <= 0) {
            throw new IllegalArgumentException("totalPrice 不能为空！");
        }

        if (order.getUserId() == null) {
            throw new IllegalArgumentException("userId 不能为空！");
        }

        if (order.getAddress() == null) {
            throw new IllegalArgumentException("userId 不能为空！");
        }

        long id = orderMapper.insertSelective(order);
        order.setId(id);
        return order;
    }
}
