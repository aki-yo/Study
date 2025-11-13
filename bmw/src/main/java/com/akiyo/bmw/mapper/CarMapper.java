package com.akiyo.bmw.mapper;

import com.akiyo.bmw.entity.Car;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CarMapper extends BaseMapper<Car> {
    List<Car> listCars(Integer size, Integer num);

    Page<Car> selectCar(Page page, @Param("modelName") String modelName, @Param("series") String series, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
}
