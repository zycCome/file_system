package com.zyc.file_system.repositry;


import com.zyc.file_system.entity.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemUserRepository extends JpaRepository<SystemUser,String>, JpaSpecificationExecutor<SystemUser> {

    SystemUser findByUsername(String username);

    Page<SystemUser> findByUsernameContaining(String username,Pageable page);

    int countByRegionId(String regionid);
}
