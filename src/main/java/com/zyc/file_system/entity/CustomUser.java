package com.zyc.file_system.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

public class CustomUser extends User {

    private String regionId;

    private List<String> authorizedDirectory;

    public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,String regionId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.regionId = regionId;
    }

    public List<String> getAuthorizedDirectory() {
        return authorizedDirectory;
    }

    public void setAuthorizedDirectory(List<String> authorizedDirectory) {
        this.authorizedDirectory = authorizedDirectory;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
