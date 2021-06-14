package com.catmmao.wechatshop.dao;

import com.catmmao.wechatshop.dao.mapper.GoodsMapper;
import com.catmmao.wechatshop.model.generated.Goods;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class GoodsDao {
    private final SqlSessionFactory sqlSessionFactory;

    public GoodsDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public Goods insert(Goods goods) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            GoodsMapper goodsMapper = sqlSession.getMapper(GoodsMapper.class);
            long id = goodsMapper.insert(goods);

            goods.setId(id);
            return goods;
        }
    }
}
