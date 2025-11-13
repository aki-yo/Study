package com.akiyo.bmw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("bmw_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username; // 用户名
    private String password; // 密码（加密存储）
    private String realName; // 真实姓名
    private String phone; // 手机号
    private String email; // 邮箱
    private String address; // 地址
    private Integer status; // 状态：0-禁用 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}