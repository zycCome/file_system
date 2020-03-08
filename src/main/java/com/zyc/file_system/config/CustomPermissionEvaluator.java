package com.zyc.file_system.config;


import com.zyc.file_system.entity.CustomUser;
import com.zyc.file_system.util.DirectoryUtil;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {


    /**
     *
     * @param authentication
     * @param path  文件或目录的path
     * @param request
     * @return
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object path, Object request) {
        String pathString = (String) path;
        CustomUser user = (CustomUser) authentication.getPrincipal();
        for (GrantedAuthority authority : user.getAuthorities()) {
            String authorityString = authority.getAuthority();
            if(authorityString.equals("ROLE_SUPERADMIN")){//超级用户不看目录权限
                return true;
            }
        }
        for (String s : user.getAuthorizedDirectory()) {
            if(s.equals(pathString) || DirectoryUtil.isChildrenDirectory(pathString,s)){
                return true;//满足一个即可
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
