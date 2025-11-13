package com.akiyo.bmw.service;

import com.akiyo.bmw.entity.Car;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface CarService extends IService<Car> {

    public List<Car> listCars(Integer size,Integer num);

    List<Car> getOnSaleCars(Integer num, Integer size);

    boolean updateCar(Car car);

    Long addCar(Car car);

    boolean deleteCar(Long id);

    List<Car> queryPage(Integer pageNum, Integer pageSize, String modelName, String series, BigDecimal minPrice, BigDecimal maxPrice);
}
