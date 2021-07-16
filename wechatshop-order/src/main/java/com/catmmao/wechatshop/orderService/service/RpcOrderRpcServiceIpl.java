package com.catmmao.wechatshop.orderService.service;

import java.util.List;

import com.catmmao.wechatshop.api.data.DbDataStatus;
import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import com.catmmao.wechatshop.api.data.RpcOrderResponse;
import com.catmmao.wechatshop.api.exception.HttpException;
import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;
import com.catmmao.wechatshop.api.generated.OrderMapper;
import com.catmmao.wechatshop.api.rpc.OrderRpcService;
import com.catmmao.wechatshop.orderService.dao.OrderGoodsDao;
import org.apache.dubbo.config.annotation.DubboService;

// 本模块的版本号，在 application.yml 中定义
@DubboService(version = "${wechatshop.order.version}")
public class RpcOrderRpcServiceIpl implements OrderRpcService {
    private final OrderGoodsDao orderGoodsDao;
    private final OrderMapper orderMapper;

    public RpcOrderRpcServiceIpl(OrderGoodsDao orderGoodsDao,
                                 OrderMapper orderMapper) {
        this.orderGoodsDao = orderGoodsDao;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order createOrder(List<GoodsOnlyContainGoodsIdAndNumber> goodsOnlyContainGoodsIdAndNumberList,
                             Order order) {
        order = orderGoodsDao.insertOrder(order);
        orderGoodsDao.insertMultiOrderGoods(goodsOnlyContainGoodsIdAndNumberList, order.getId());

        return order;
    }

    /**
     * 删除订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 删除后的订单信息
     */
    @Override
    public RpcOrderResponse deleteOrderByOrderId(long orderId, long userId) {
        Order order = getOrderByOrderId(orderId);

        if (userId != order.getUserId()) {
            throw HttpException.forbidden("无权访问");
        }

        // 删除订单
        order.setStatus(DbDataStatus.DELETE.getName());
        orderMapper.updateByPrimaryKeySelective(order);

        List<OrderGoodsMapping> list = getListOfOrderGoodsMappingByOrderId(orderId);
        RpcOrderResponse result = new RpcOrderResponse();
        result.setOrder(order);
        result.setGoodsList(list);
        return result;
    }

    /**
     * 根据订单ID获取商品列表
     *
     * @param orderId 订单ID
     * @return 商品列表
     */
    private List<OrderGoodsMapping> getListOfOrderGoodsMappingByOrderId(long orderId) {
        return orderGoodsDao.getListOfOrderGoodsMappingByOrderId(orderId);
    }

    /**
     * 根据订单ID获取订单
     *
     * @param orderId 订单ID
     * @return 订单
     */
    private Order getOrderByOrderId(long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw HttpException.resourceNotFound("找不到该订单");
        }

        return order;
    }
}
