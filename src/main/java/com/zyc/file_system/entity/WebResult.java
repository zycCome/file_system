package com.zyc.file_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebResult {

    private int code;//0异常,1:正常

    private String message;//提示消息

    private Object data;//响应数据


    public static WebResult success(){
        return new WebResult(1,"请求成功",null);
    }

    public static WebResult success(Object data){
        return new WebResult(1,"请求成功",data);
    }

    public static WebResult successWithMessage(String  message){
        return new WebResult(1,message,null);
    }

    public static WebResult fail(){
        return new WebResult(0,"未知异常，请联系管理员",null);
    }

    public static WebResult fail(String message){
        return new WebResult(0,message,null);
    }


}
