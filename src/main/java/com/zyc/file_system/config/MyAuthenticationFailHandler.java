package com.zyc.file_system.config;


import com.zyc.file_system.entity.WebResult;
import com.zyc.file_system.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component
public class MyAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("登录失败:" + exception.getMessage());
        log.info("username=>" + request.getParameter("username"));

        WebResult webResult;
        if(exception instanceof DisabledException){
            webResult = WebResult.fail("账号被锁定");
        }else{
            webResult = WebResult.fail("用户名或密码错误");
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtils.toJson(webResult));

    }
}

