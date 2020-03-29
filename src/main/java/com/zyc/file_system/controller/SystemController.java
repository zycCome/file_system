package com.zyc.file_system.controller;


import com.zyc.file_system.entity.CustomUser;
import com.zyc.file_system.entity.SystemUser;
import com.zyc.file_system.entity.WebResult;
import com.zyc.file_system.service.SystemUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/systemUser")
@Slf4j
public class SystemController {

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("")
    @RolesAllowed("SUPERADMIN")
    public WebResult save(@Valid @RequestBody SystemUser systemUser, BindingResult bindingResult){

        if(!systemUser.getRole().equals("USER") && !systemUser.getRole().equals("SUPERADMIN"))
            return WebResult.fail("角色名非法");

        SystemUser preUser = systemUserService.findByUsername(systemUser.getUsername());
        if(preUser != null)
            return WebResult.fail("用户名已存在");

        systemUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
        systemUserService.save(systemUser);

        return WebResult.success();

    }



    /**
     * 超级管理员修改用户
     * @param systemUser
     * @return
     */
    @PutMapping("")
    @RolesAllowed("SUPERADMIN")
    public WebResult update(@RequestBody SystemUser systemUser){
        String id = systemUser.getId();
        SystemUser pre = systemUserService.findById(id);
        if(pre == null)
            return WebResult.fail("参数非法");

        if(!systemUser.getRole().equals("USER") && !systemUser.getRole().equals("SUPERADMIN"))
            return WebResult.fail("角色名非法");
        if(StringUtils.isEmpty(systemUser.getPassword())){
            systemUser.setPassword(pre.getPassword());//不改动
        }else{
            systemUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
        }


        systemUserService.save(systemUser);

        return WebResult.success();

    }

    @GetMapping("/page")
    @RolesAllowed("SUPERADMIN")
    public WebResult page(int page,int size,String username){
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<SystemUser> page_system = systemUserService.findByUsernameContaining(username,pageable);

        return WebResult.success(page_system);
    }


    /**
     * 只能修改自己的密码
     * @param password
     * @return
     */
    @PostMapping("/password")
    public WebResult password(String password){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) auth.getPrincipal();

        SystemUser systemUser = systemUserService.findByUsername(user.getUsername());
        systemUser.setPassword(passwordEncoder.encode(password));
        systemUserService.save(systemUser);

        return WebResult.success();

    }







}
