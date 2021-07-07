package com.catmmao.wechatshop.dao;

import java.util.List;

import com.catmmao.wechatshop.dao.mapper.UserMapper;
import com.catmmao.wechatshop.model.generated.User;
import com.catmmao.wechatshop.model.generated.UserExample;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    private final UserMapper userMapper;

    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUserByTel(String tel) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTelEqualTo(tel);
        List<User> list = userMapper.selectByExample(userExample);

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
