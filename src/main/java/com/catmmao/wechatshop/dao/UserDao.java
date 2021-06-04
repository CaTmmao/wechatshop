package com.catmmao.wechatshop.dao;

import java.util.List;

import com.catmmao.wechatshop.dao.mapper.UserMapper;
import com.catmmao.wechatshop.model.User;
import com.catmmao.wechatshop.model.UserExample;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    private final SqlSessionFactory sqlSessionFactory;

    public UserDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insertUser(User user) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insert(user);
        }
    }

    public User getUserByTel(String tel) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            UserExample userExample = new UserExample();
            userExample.createCriteria().andTelEqualTo(tel);
            List<User> list = userMapper.selectByExample(userExample);
            return list.get(0);
        }
    }
}
