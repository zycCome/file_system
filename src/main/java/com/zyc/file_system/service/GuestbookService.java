package com.zyc.file_system.service;

import com.zyc.file_system.entity.Guestbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuestbookService {

    Guestbook save(Guestbook guestbook);

    Page<Guestbook> findAll(Pageable pageable,String regionCode);

    Guestbook findById(String id);


    void delete(String id);

    Page<Guestbook> findAllByCode(Pageable pageable,String code);



}
