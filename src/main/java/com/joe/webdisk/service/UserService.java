package com.joe.webdisk.service;

import com.joe.webdisk.entity.MyFile;
import com.joe.webdisk.entity.User;

import java.util.List;

public interface UserService {
    boolean insert(User user);

    boolean deleteById(Integer userId);

    User queryUserByEmail(String email);

    User queryById(Integer userId);

    User queryByEmailAndPwd(String email, String password);

    List<User> queryAll();

    boolean update(User user);

    List<MyFile> getUsers();

    Integer getUsersCount();
}
