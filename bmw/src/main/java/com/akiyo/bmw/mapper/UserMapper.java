package com.akiyo.bmw.mapper;

import com.akiyo.bmw.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    Integer changeUser(User user);
}
