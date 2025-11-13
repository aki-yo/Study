package com.akiyo.bmw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bmw_car")
public class Car {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String modelName; // 车型名称
    private String series; // 车系
    private String description; // 车型描述
    private BigDecimal price; // 价格
    private String color; // 颜色
    private String engine; // 发动机信息
    private String transmission; // 变速箱
    private Integer inventory; // 库存数量
    private Integer status; // 状态：0-下架 1-上架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
