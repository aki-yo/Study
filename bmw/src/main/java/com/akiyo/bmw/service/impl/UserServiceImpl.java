package com.akiyo.bmw.service.impl;

import com.akiyo.bmw.entity.User;
import com.akiyo.bmw.mapper.UserMapper;
import com.akiyo.bmw.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserMapper userMapper;
    @Override
    public Integer changeUser(User user) {
        //updateById(user);
        return userMapper.changeUser(user);
        /*return lambdaUpdate()
                .eq(User::getId,user.getId())
                .set(StringUtils.isNotBlank(user.getUsername()),User::getUsername,user.getUsername())
                .update();*/
    }
}
