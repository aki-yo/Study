package com.akiyo.bmw.service;

import com.akiyo.bmw.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Order> {
    Boolean pay(Long id);

    Boolean cancelOrder(Long id);

    Long addOrder(Long userId, Long carId);

    Long addOrder(Order order);
}
