package com.akiyo.bmw.service;

import com.akiyo.bmw.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    Integer changeUser(User user);
}
