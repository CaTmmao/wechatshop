package com.catmmao.wechatshop.service;

import com.catmmao.wechatshop.dao.UserDao;
import com.catmmao.wechatshop.model.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUserIfNotExist(String tel) {
        User user = new User();
        user.setTel(tel);

        // 没有判断用户是否存在在数据库的步骤，因为是多线程运行，可能会有问题，所以这里直接将用户数据插入到数据库
        try {
            userDao.insertUser(user);
        } catch (PersistenceException e) {
            return userDao.getUserByTel(tel);
        }

        return user;
    }
}
