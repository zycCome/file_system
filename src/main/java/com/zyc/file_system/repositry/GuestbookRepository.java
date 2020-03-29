package com.zyc.file_system.repositry;

import com.zyc.file_system.entity.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GuestbookRepository extends JpaRepository<Guestbook,String>, JpaSpecificationExecutor<Guestbook> {
}
