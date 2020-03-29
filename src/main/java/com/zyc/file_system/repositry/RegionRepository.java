package com.zyc.file_system.repositry;

import com.zyc.file_system.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region,String>, JpaSpecificationExecutor<Region> {

    Region findByCodeAndDel(String code,Integer del);

    Region findByCodeAndDelAndIdNot(String code,Integer del,String id);

    List<Region> findByPathLikeAndDel(String path,Integer del);


    @Query("select code from Region where path like %:code% and del = 0")
    List<String> findCodeByPath(String code);

}
