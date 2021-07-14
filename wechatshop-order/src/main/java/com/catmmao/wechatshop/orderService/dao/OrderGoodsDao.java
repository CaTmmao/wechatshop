package com.catmmao.wechatshop.orderService.dao;

import java.util.List;

import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;
import com.catmmao.wechatshop.api.generated.OrderGoodsMappingMapper;
import com.catmmao.wechatshop.api.generated.OrderMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OrderGoodsDao {
    private final SqlSessionFactory sqlSessionFactory;
    private final OrderMapper orderMapper;

    public OrderGoodsDao(SqlSessionFactory sqlSessionFactory,
                         OrderMapper orderMapper) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.orderMapper = orderMapper;
    }

    public void insertMultiOrderGoods(List<GoodsOnlyContainGoodsIdAndNumber> goodsOnlyContainGoodsIdAndNumbers,
                                      long orderId) {
        // 添加到数据库的 order_goods 表
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            OrderGoodsMappingMapper mapper = sqlSession.getMapper(OrderGoodsMappingMapper.class);
            goodsOnlyContainGoodsIdAndNumbers.forEach(item -> {
                OrderGoodsMapping object = new OrderGoodsMapping();
                object.setGoodsId(item.getGoodsId());
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
}
