package com.zyc.file_system.controller;



import com.zyc.file_system.entity.WebResult;
import com.zyc.file_system.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
public class LogincController {



    @GetMapping("/auth")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public WebResult auth(){

        return WebResult.fail("请先登录");

    }



    @GetMapping("/logout")
    public WebResult logout(){
        return WebResult.successWithMessage("注销成功");
    }








}
