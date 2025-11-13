package com.akiyo.bmw.controller;

import com.akiyo.bmw.Result;
import com.akiyo.bmw.entity.Car;
import com.akiyo.bmw.service.impl.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/car")
public class CarController {
    private final CarService carService;

    @GetMapping(value = "/getCars")
    public Result<List<Car>> getCars(
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) Integer size,
            @RequestParam(value = "pageNum",defaultValue = "1",required = false) Integer num){
        log.info("size"+size+"num"+num);
        return Result.ok(carService.listCars(size,num));
    }

    @GetMapping("/onSale")
    public Result<List<Car>> getOnSaleCars(@RequestParam("pageNum") Integer num,@RequestParam("pageSize")Integer size){
        return Result.ok(carService.getOnSaleCars(num,size));
//        return carService.getOnSaleCars(num,size);
    }

    @PutMapping("/putCar/{id}")
    public Result<Boolean> updateCar(@PathVariable("id") Integer id, @RequestBody Car car){
        return Result.ok(carService.updateCar(car));
    }

    @PostMapping
    public Result<Long> addCar(@RequestBody Car car){
        return Result.ok(carService.addCar(car));
    }

    @DeleteMapping(value = "/{id}")
    public Result<Boolean> deleteCar(@PathVariable Long id){
        return Result.ok(carService.deleteCar(id));
    }

    @GetMapping("/page")
    public ResponseEntity<List<Car>> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
//            @RequestBody Car
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String series,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        log.info("size:{},num:{}",pageSize,pageNum);
        List<Car> pageInfo = carService.queryPage(pageNum, pageSize, modelName, series, minPrice, maxPrice);
        return ResponseEntity.ok(pageInfo);
    }
}
