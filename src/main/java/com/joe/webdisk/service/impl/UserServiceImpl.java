package com.joe.webdisk.service.impl;

import com.joe.webdisk.entity.MyFile;
import com.joe.webdisk.entity.User;
import com.joe.webdisk.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends BaseService implements UserService {
    @Override
    public boolean insert(User user) {
        return userMapper.insert(user) == 1;
    }

    @Override
    public boolean deleteById(Integer userId) {
        return userMapper.deleteById(userId) == 1;
    }

    @Override
    public User queryUserByEmail(String email) {
        return userMapper.queryUserByEmail(email);
    }
    @Override
    public User queryById(Integer userId) {
        return userMapper.queryById(userId);
    }
    @Override
    public User queryByEmailAndPwd(String email, String password) {
        return userMapper.queryByEmailAndPwd(email,password);
    }

    @Override
    public List<User> queryAll() {
        return userMapper.queryAll();
    }

    @Override
    public boolean update(User user) {
        return userMapper.update(user) == 1;
    }

    @Override
    public List<MyFile> getUsers() {
        return userMapper.getUsers();
    }

    @Override
    public Integer getUsersCount() {
        return userMapper.getUsersCount();
    }
}
