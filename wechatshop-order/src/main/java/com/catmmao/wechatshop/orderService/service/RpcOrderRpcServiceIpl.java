package com.catmmao.wechatshop.orderService.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import com.catmmao.wechatshop.api.data.DbDataStatus;
import com.catmmao.wechatshop.api.data.GoodsIdAndNumber;
import com.catmmao.wechatshop.api.data.PaginationResponse;
import com.catmmao.wechatshop.api.data.RpcOrderResponse;
import com.catmmao.wechatshop.api.exception.HttpException;
import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.generated.OrderExample;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;
import com.catmmao.wechatshop.api.generated.OrderGoodsMappingMapper;
import com.catmmao.wechatshop.api.generated.OrderMapper;
import com.catmmao.wechatshop.api.rpc.OrderRpcService;
import com.catmmao.wechatshop.orderService.dao.OrderGoodsDao;
import org.apache.dubbo.config.annotation.DubboService;

// 本模块的版本号，在 application.yml 中定义
@DubboService(version = "${wechatshop.order.version}")
public class RpcOrderRpcServiceIpl implements OrderRpcService {
    private final OrderGoodsDao orderGoodsDao;
    private final OrderMapper orderMapper;
    private final OrderGoodsMappingMapper orderGoodsMappingMapper;

    public RpcOrderRpcServiceIpl(OrderGoodsDao orderGoodsDao,
                                 OrderMapper orderMapper,
                                 OrderGoodsMappingMapper orderGoodsMappingMapper) {
        this.orderGoodsDao = orderGoodsDao;
        this.orderMapper = orderMapper;
        this.orderGoodsMappingMapper = orderGoodsMappingMapper;
    }

    @Override
    public Order createOrder(List<GoodsIdAndNumber> listOfGoodsIdAndNumber,
                             Order order) {
        order = orderGoodsDao.insertOrder(order);
        orderGoodsDao.insertMultiOrderGoods(listOfGoodsIdAndNumber, order.getId());

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

        return generateRpcOrderResponseByOrderId(order.getId());
    }

    /**
     * 获取当前用户名下的所有订单
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @param status   订单状态：pending 待付款 paid 已付款 delivered 物流中 received 已收货
     * @return 订单列表
     */
    @Override
    public PaginationResponse<RpcOrderResponse> getAllOrders(long userId, int pageNum, int pageSize, String status) {
        List<Order> orderList = orderGoodsDao.getListOfOrderWithPagination(pageNum, pageSize, status, userId);
        List<Long> listOfOrderId = orderList.stream().map(Order::getId).collect(toList());

        List<OrderGoodsMapping> listOfOrderGoodsMapping =
            orderGoodsDao.getListOfOrderGoodsMappingByListOfOrderId(listOfOrderId);

        Map<Long, List<OrderGoodsMapping>> mapOfOrderIdToOrderGoodsMappingList = listOfOrderGoodsMapping.stream()
            .collect(groupingBy(OrderGoodsMapping::getOrderId, toList()));

        List<RpcOrderResponse> data = orderList.stream()
            .map(order -> {
                RpcOrderResponse item = new RpcOrderResponse();
                item.setOrder(order);
                item.setGoodsList(mapOfOrderIdToOrderGoodsMappingList.get(order.getId()));
                return item;
            })
            .collect(toList());

        int count = (int) getCountOfUserHasHowManyOrder(status);
        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        return new PaginationResponse<>(pageSize, pageNum, totalPage, data);
    }

    /**
     * 查看当前用户XX状态的订单共有多少个
     *
     * @param status 订单状态
     * @return 订单数量
     */
    private long getCountOfUserHasHowManyOrder(String status) {
        OrderExample orderExample = new OrderExample();
        setOrderExampleCriteriaOfStatus(orderExample, status);
        return orderMapper.countByExample(orderExample);
    }

    /**
     * 给 orderExample 设置 status 条件
     *
     * @param orderExample orderExample
     * @param status       status 字段
     * @return 设置了 status 条件的 orderExample, 可继续设置其他条件
     */
    public OrderExample.Criteria setOrderExampleCriteriaOfStatus(OrderExample orderExample, String status) {
        if (status == null) {
            return orderExample.createCriteria().andStatusNotEqualTo(DbDataStatus.DELETE.getName());
        } else {
            return orderExample.createCriteria().andStatusEqualTo(status);
        }
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
    public Order getOrderByOrderId(long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw HttpException.resourceNotFound("找不到该订单");
        }

        return order;
    }

    /**
     * 更新订单
     *
     * @param order 待更新的订单信息
     * @return 更新后完整的订单信息
     */
    @Override
    public RpcOrderResponse updateOrder(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
        return generateRpcOrderResponseByOrderId(order.getId());
    }

    /**
     * 根据订单ID生成 RpcOrderResponse 对象
     *
     * @param orderId 订单ID
     * @return 生成的 RpcOrderResponse 对象
     */
    private RpcOrderResponse generateRpcOrderResponseByOrderId(long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        List<OrderGoodsMapping> list = getListOfOrderGoodsMappingByOrderId(orderId);

        RpcOrderResponse result = new RpcOrderResponse();
        result.setOrder(order);
        result.setGoodsList(list);
        return result;
    }
}
