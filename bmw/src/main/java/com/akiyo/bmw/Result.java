package com.akiyo.bmw;


import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private Serializable serializable = 1L;

    private String code;

    private String message;

    private T data;


    public static <T> Result<T> ok(T data){
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(T data){
        Result<T> result = new Result<>();
        result.setCode("400");
        result.setData(data);
        return result;
    }

}
