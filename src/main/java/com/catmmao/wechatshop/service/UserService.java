package com.catmmao.wechatshop.service;

import java.util.Date;
import java.util.Optional;

import com.catmmao.wechatshop.dao.UserDao;
import com.catmmao.wechatshop.dao.mapper.UserMapper;
import com.catmmao.wechatshop.model.generated.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    public UserService(UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    public User createUserIfNotExist(String tel) {
        User user = new User();
        user.setTel(tel);
        user.setCreatedAt(new Date());

        // 没有判断用户是否存在在数据库的步骤，因为是多线程运行，可能会有问题，所以这里直接将用户数据插入到数据库
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            return userDao.getUserByTel(tel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public Optional<User> getUserByTel(String tel) {
        return Optional.ofNullable(userDao.getUserByTel(tel));
    }
}
