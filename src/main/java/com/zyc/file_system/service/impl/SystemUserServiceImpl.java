package com.zyc.file_system.service.impl;


import com.zyc.file_system.entity.SystemUser;
import com.zyc.file_system.repositry.SystemUserRepository;
import com.zyc.file_system.service.SystemUserService;
import com.zyc.file_system.util.DirectoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Override
    @Transactional(readOnly = true)
    public SystemUser findByUsername(String username) {
        return systemUserRepository.findByUsername(username);
    }

    @Override
    public SystemUser save(SystemUser systemUser) {
        /**
         * 处理用户的授权目录，如果同时存在父子目录。只保留大的目录
         */
        List<String> list = systemUser.getAuthorizedDirectory();
        List<String> list2 = new ArrayList<>();
        for (String s : list) {
            list2.add(s);
        }

        Iterator<String> iterator =  list.iterator();
        while (iterator.hasNext()){
            String directory = iterator.next();
            for (String parent : list2) {
                if(!directory.equals(parent) && DirectoryUtil.isChildrenDirectory(directory,parent)){
                    //这是小的目录
                    iterator.remove();
                    break;//移除了就结束了
                }
            }

        }
        systemUser.setAuthorizedDirectory(list);
        return systemUserRepository.save(systemUser);
    }


    @Override
    public SystemUser findById(String id) {
        return systemUserRepository.findById(id).get();
    }

    @Override
    public Page<SystemUser> findByUsernameContaining(String username,Pageable page) {
        Page<SystemUser> page_system= systemUserRepository.findByUsernameContaining(username,page);
        return page_system;
    }


}
