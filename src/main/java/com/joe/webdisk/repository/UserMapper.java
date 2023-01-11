package com.joe.webdisk.repository;

import com.joe.webdisk.entity.MyFile;
import com.joe.webdisk.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User user);

    int deleteById(Integer userId);

    User queryUserByEmail(String email);

    User queryById(Integer userId);

    User queryByEmailAndPwd(String email, String password);

    List<User> queryAll();

    int update(User user);

    List<MyFile> getUsers();

    Integer getUsersCount();
}
