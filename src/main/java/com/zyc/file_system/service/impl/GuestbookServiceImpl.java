package com.zyc.file_system.service.impl;

import com.zyc.file_system.entity.Guestbook;
import com.zyc.file_system.repositry.GuestbookRepository;
import com.zyc.file_system.repositry.RegionRepository;
import com.zyc.file_system.service.GuestbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;


@Service
@Transactional
public class GuestbookServiceImpl implements GuestbookService {

    @Autowired
    private GuestbookRepository guestbookRepository;


    @Autowired
    private RegionRepository regionRepository;

    public Guestbook save(Guestbook guestbook){
        return guestbookRepository.save(guestbook);
    }

    public Page<Guestbook> findAll(Pageable pageable,String regionId){
        //检查是否存在子区域
        List<String > codes = regionRepository.findCodeByPath("/"+regionId);
        Specification<Guestbook> s1 = new Specification<Guestbook>() {
            @Override
            public Predicate toPredicate(Root<Guestbook> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("region"));
                for (String code : codes) {
                    in.value(code);
                }
                Predicate p1 = criteriaBuilder.equal(root.get("del"),0);
                return criteriaBuilder.and(p1,in);
            }
        };


        return guestbookRepository.findAll(s1,pageable);
    }

    public Page<Guestbook> findAllByCode(Pageable pageable,String code){
        //检查是否存在子区域
        Specification<Guestbook> s1 = new Specification<Guestbook>() {
            @Override
            public Predicate toPredicate(Root<Guestbook> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                Predicate p1 = criteriaBuilder.equal(root.get("del"),0);
                Predicate p2 = criteriaBuilder.equal(root.get("open"),1);
                Predicate p3 = criteriaBuilder.equal(root.get("status"),2);
                Predicate p4 = criteriaBuilder.equal(root.get("region"),code);
                return criteriaBuilder.and(p1,p2,p3,p4);
            }
        };


        return guestbookRepository.findAll(s1,pageable);
    }

    @Override
    public Guestbook findById(String id) {
        return guestbookRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {
        guestbookRepository.findById(id).get().setDel(1);
    }


}
