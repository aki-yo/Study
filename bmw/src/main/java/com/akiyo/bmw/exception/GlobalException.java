package com.akiyo.bmw.exception;


import com.akiyo.bmw.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e){
        System.out.println(e.getMessage());
        return Result.fail(e.getMessage());
    }
}
