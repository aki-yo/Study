package com.akiyo.bmw.controller;

import com.akiyo.bmw.Result;
import com.akiyo.bmw.entity.Car;
import com.akiyo.bmw.entity.Order;
import com.akiyo.bmw.service.CarService;
import com.akiyo.bmw.service.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final CarService carService;

    private final OrderService orderService;
    @PostMapping
    public Result<Long> addOrder(@RequestBody Order order){
        return Result.ok(orderService.addOrder(order));
    }

    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id){
        return Result.ok(orderService.getById(id));
    }

    @PatchMapping("/pay/{id}")
    public Result<Boolean> pay(@PathVariable Long id){
        return Result.ok(orderService.pay(id));
    }

    @PatchMapping("/cancel/{id}")
    public Result<Boolean> cancelOrder(@PathVariable Long id){
        return Result.ok(orderService.cancelOrder(id));
    }

    @GetMapping("/list")
    public Result<List<Order>> listOrder(@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "10") Integer pageSize,@RequestParam(required = false) String carModelName){
//        return Result.ok(orderService.list(new Page<>(pageNum,pageSize)).stream().sorted().toList() );
        return Result.ok(orderService.lambdaQuery()
                        .like(StringUtils.isNotBlank(carModelName),Order::getCarModelName,carModelName)
                .orderByDesc(Order::getCreateTime).list(new Page<>(pageNum,pageSize)));
    }
}
