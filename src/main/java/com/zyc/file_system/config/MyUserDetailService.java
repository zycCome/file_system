package com.zyc.file_system.config;

import com.zyc.file_system.entity.CustomUser;
import com.zyc.file_system.entity.SystemUser;
import com.zyc.file_system.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private SystemUserService systemUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SystemUser systemUser = systemUserService.findByUsername(username);
        //如果查不到用户名，这里可以抛出UsernameNotFoundException异常
        if(systemUser == null)
            throw new UsernameNotFoundException(username);

        //根据username查询权限，这里假设从任意位置查到权限是auth
        List<GrantedAuthority> authorities= new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_"+systemUser.getRole()));
        //User是系统自带的UserDetails实现类，4个状态其中一个为false就会抛异常
        return new CustomUser(username, systemUser.getPassword(), systemUser.getEnable(), true, true, true, authorities,systemUser.getRegionId());
    }
}
