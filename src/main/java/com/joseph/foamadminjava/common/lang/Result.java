package com.joseph.foamadminjava.common.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Joseph.Liu
 */
@Data
public class Result implements Serializable {
    private int code;
    private String msg;
    private Object data;

    public static Result success(Object data){
        return result(200,"success",data);
    }

    public static Result fail(String msg){
        return result(400,msg,null);
    }

    private static Result result(int code, String msg, Object data){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
