package com.zyc.file_system.service;

import com.zyc.file_system.entity.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SystemUserService {

    SystemUser findByUsername(String username);

    SystemUser save(SystemUser systemUser);


    SystemUser findById(String id);

    Page<SystemUser> findByUsernameContaining(String username,Pageable page);
}
