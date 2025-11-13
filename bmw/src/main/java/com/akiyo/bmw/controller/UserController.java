package com.akiyo.bmw.controller;

import com.akiyo.bmw.Result;
import com.akiyo.bmw.entity.User;
import com.akiyo.bmw.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<List<User>> getUsers(@RequestParam(defaultValue = "2",required = false) Integer pageNum,@RequestParam(defaultValue = "10") Integer pageSize){
        return Result.ok(userService.list(new Page<>(pageNum,pageSize)));
    }

    @PostMapping
    public Result<Long> addUser(@RequestBody User user){
        userService.save(user);
        return Result.ok(user.getId());
    }
    @PutMapping
    public Result<Integer> changeUser(@RequestBody User user){
        return Result.ok(userService.changeUser(user));
    }
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id){
        return Result.ok(userService.removeById(id));
    }

    @GetMapping("/{id}")
    public Result<User> queryUser(@PathVariable Long id){
        return Result.ok(userService.getById(id));
    }

    private String stamp(){
        String data = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String uuid = UUID.randomUUID().toString().substring(0,8).toUpperCase();
        return "bmw"+data+uuid;
    }
}
