package com.akiyo.bmw.service.impl;

import com.akiyo.bmw.entity.Car;
import com.akiyo.bmw.mapper.CarMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService extends ServiceImpl<CarMapper, Car> implements com.akiyo.bmw.service.CarService {

    private final CarMapper carMapper;
    @Override
    public List<Car> listCars(Integer size,Integer num) {
//        LambdaQueryWrapper<Car> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        return baseMapper.selectPage(new Page<>(num,size),
//                lambdaQueryWrapper).getRecords();
//        return this.lambdaQuery().orderByDesc(Car::getColor).list();
        return this.list(new Page<>(num,size));
//        return lambdaQuery().list(new Page<>(num,size));
//        return carMapper.listCars(size,num);
//        return this.list();
    }

    @Override
    public List<Car> getOnSaleCars(Integer num, Integer size) {
        return lambdaQuery().eq(Car::getStatus,"1")
                .list(new Page<>(num,size));
        // 这个方式也行
        /*return lambdaQuery().eq(Car::getStatus,"1")
                .page(new Page<>(num,size)).getRecords();*/
    }

    @Override
    public boolean updateCar(Car car) {
        return lambdaUpdate().eq(Car::getId,car.getId())
                .set(car.getStatus()!= null ,Car::getStatus,car.getStatus()).update();
    }

    @Override
    public Long addCar(Car car) {
        save(car);
        return car.getId();
    }

    @Override
    public boolean deleteCar(Long id) {
        return this.removeById(id);
    }

    @Override
    public List<Car> queryPage(Integer pageNum, Integer pageSize, String modelName, String series, BigDecimal minPrice, BigDecimal maxPrice) {
        return carMapper.selectCar(new Page(pageNum,pageSize), modelName,series,minPrice,maxPrice).getRecords();
        // 下面这个也行
        /*return lambdaQuery()
                .like(StringUtils.isNotBlank(modelName),Car::getModelName,modelName)
                .like(StringUtils.isNotBlank(series),Car::getSeries,series)
                .ge(minPrice!=null,Car::getPrice,minPrice)
                .le(maxPrice!=null,Car::getPrice,maxPrice)
                .list(new Page<>(pageNum,pageSize));*/
    }
}
