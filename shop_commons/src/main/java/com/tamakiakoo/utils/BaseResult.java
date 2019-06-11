package com.tamakiakoo.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResult {

    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_FAIL = 500;

    private int status;
    private String message;
    private Object data;

    public static BaseResult success(){
        return createResult(STATUS_SUCCESS, "成功~", null);
    }

    public static BaseResult success(String message){
        return createResult(STATUS_SUCCESS, message, null);
    }

    public static BaseResult success(String message,Object data){
        return createResult(STATUS_SUCCESS, message, data);
    }

    public static BaseResult fail(){
        return createResult(STATUS_FAIL, "失败~", null);
    }

    public static BaseResult fail(String message){
        return createResult(STATUS_FAIL, message, null);
    }

    public static BaseResult fail(String message,Object data){
        return createResult(STATUS_FAIL, message, data);
    }


    private static BaseResult createResult(int status,String message,Object data){
        BaseResult baseResult = new BaseResult();
        baseResult.setStatus(status);
        baseResult.setMessage(message);
        baseResult.setData(data);
        return baseResult;
    }

}
