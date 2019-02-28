package com.example.service;

import com.example.entity.UserInfo;

import java.util.List;

public interface UserService {

    Integer saveUser(UserInfo userInfo);

    UserInfo getUserById(Integer userId);

    List<UserInfo> getUserList();
}
