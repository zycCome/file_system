package com.zyc.file_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyc.file_system.entity.CustomUser;
import com.zyc.file_system.entity.SystemUser;
import com.zyc.file_system.entity.WebResult;
import com.zyc.file_system.service.SystemUserService;
import com.zyc.file_system.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class SuccessAuthenticationHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;

    public SuccessAuthenticationHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    private SystemUserService systemUserService;

    @Transactional(readOnly = true)
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        log.info("登录成功");
        httpServletResponse.setStatus(HttpStatus.OK.value());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) auth.getPrincipal();

        SystemUser systemUser = systemUserService.findByUsername(user.getUsername());//加入目录权限
        user.setAuthorizedDirectory(systemUser.getAuthorizedDirectory());
        systemUser.setPassword("");//密码隐藏

        WebResult webResult =WebResult.success(systemUser);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JsonUtils.toJson(webResult));
    }
}
