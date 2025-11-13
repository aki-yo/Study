package com.akiyo.bmw.service.impl;

import com.akiyo.bmw.entity.Car;
import com.akiyo.bmw.entity.Order;
import com.akiyo.bmw.entity.User;
import com.akiyo.bmw.mapper.OrderMapper;
import com.akiyo.bmw.service.CarService;
import com.akiyo.bmw.service.OrderService;
import com.akiyo.bmw.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final CarService carService;

    private final UserService userService;
    @Override
    public Boolean pay(Long id) {
        return lambdaUpdate().eq(Order::getId,id)
                .set(Order::getOrderStatus,1)
                .update();

    }

    @Override
    public Boolean cancelOrder(Long id) {
        return lambdaUpdate().eq(Order::getId,id)
                .set(Order::getOrderStatus,3)
                .set(Order::getUpdateTime,LocalDateTime.now())
                .update();
    }

    @Override
    public Long addOrder(Long userId, Long carId) {
        return null;
    }

    @Override
    public Long addOrder(Order order) {
        order.setOrderStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setOrderNo(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + UUID.randomUUID().toString().substring(0,8));

        Car car = carService.getById(order.getCarId());
        order.setCarModelName(car.getModelName());

        User user = userService.getById(order.getUserId());
        order.setCustomerAddress(user.getAddress());
        order.setCustomerName(user.getUsername());
        order.setCustomerPhone(user.getPhone());

        boolean update = carService.lambdaUpdate().eq(Car::getId, order.getCarId())
                .gt(Car::getInventory, 0)
                .setSql("inventory =inventory -1")
                .update();
        if (!update){
            throw new RuntimeException("库存不足");
        }

        save(order);

        return order.getId();
    }
}
