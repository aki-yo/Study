package com.akiyo.bmw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("bmw_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo; // 订单编号
    private Long userId; // 用户ID
    private Long carId; // 车辆ID
    private String carModelName; // 车辆型号名称（冗余字段）
    private String customerName; // 客户姓名
    private String customerPhone; // 客户电话
    private String customerAddress; // 客户地址
    private BigDecimal totalPrice; // 订单总价
    private Integer orderStatus; // 订单状态：0-待支付 1-已支付 2-已完成 3-已取消
    private String paymentMethod; // 支付方式
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}