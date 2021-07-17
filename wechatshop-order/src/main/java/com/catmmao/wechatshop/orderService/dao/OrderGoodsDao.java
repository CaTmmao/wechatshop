package com.catmmao.wechatshop.orderService.dao;

import java.util.List;

import com.catmmao.wechatshop.api.data.GoodsIdAndNumber;
import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.generated.OrderExample;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;
import com.catmmao.wechatshop.api.generated.OrderGoodsMappingExample;
import com.catmmao.wechatshop.api.generated.OrderGoodsMappingMapper;
import com.catmmao.wechatshop.api.generated.OrderMapper;
import com.catmmao.wechatshop.orderService.service.RpcOrderRpcServiceIpl;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OrderGoodsDao {
    private final SqlSessionFactory sqlSessionFactory;
    private final OrderMapper orderMapper;
    private final OrderGoodsMappingMapper orderGoodsMappingMapper;
    private final RpcOrderRpcServiceIpl rpcOrderRpcServiceIpl;

    public OrderGoodsDao(SqlSessionFactory sqlSessionFactory,
                         OrderMapper orderMapper,
                         OrderGoodsMappingMapper orderGoodsMappingMapper,
                         RpcOrderRpcServiceIpl rpcOrderRpcServiceIpl) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.orderMapper = orderMapper;
        this.orderGoodsMappingMapper = orderGoodsMappingMapper;
        this.rpcOrderRpcServiceIpl = rpcOrderRpcServiceIpl;
    }

    public void insertMultiOrderGoods(List<GoodsIdAndNumber> listOfGoodsIdAndNumber,
                                      long orderId) {
        // 添加到数据库的 order_goods 表
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            OrderGoodsMappingMapper mapper = sqlSession.getMapper(OrderGoodsMappingMapper.class);
            listOfGoodsIdAndNumber.forEach(item -> {
                OrderGoodsMapping object = new OrderGoodsMapping();
                object.setGoodsId(item.getId());
                object.setOrderId(orderId);
                object.setNumber((long) item.getNumber());
                mapper.insertSelective(object);
            });
            sqlSession.commit();
        }
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

    public List<OrderGoodsMapping> getListOfOrderGoodsMappingByOrderId(long orderId) {
        OrderGoodsMappingExample example = new OrderGoodsMappingExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        return orderGoodsMappingMapper.selectByExample(example);
    }

    /**
     * 根据订单ID数组获取 OrderGoodsMapping 列表
     *
     * @param listOfOrderId list, 每个元素都是一个订单ID
     * @return list, 每个元素都是 OrderGoodsMapping
     */
    public List<OrderGoodsMapping> getListOfOrderGoodsMappingByListOfOrderId(List<Long> listOfOrderId) {
        OrderGoodsMappingExample orderGoodsMappingExample = new OrderGoodsMappingExample();
        orderGoodsMappingExample.createCriteria().andOrderIdIn(listOfOrderId);
        return orderGoodsMappingMapper.selectByExample(orderGoodsMappingExample);
    }

    /**
     * 获取当前用户名下所有订单列表，并进行分页
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @param status   订单状态：pending 待付款 paid 已付款 delivered 物流中 received 已收货
     * @param userId   用户ID
     * @return 分页后的订单列表
     */
    public List<Order> getListOfOrderWithPagination(int pageNum, int pageSize, String status, long userId) {
        OrderExample orderExample = new OrderExample();
        orderExample.setOffset((pageNum - 1) * pageSize);
        orderExample.setLimit(pageSize);
        rpcOrderRpcServiceIpl.setOrderExampleCriteriaOfStatus(orderExample, status).andUserIdEqualTo(userId);
        return orderMapper.selectByExample(orderExample);
    }
}
