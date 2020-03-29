package com.zyc.file_system.service.impl;

import com.zyc.file_system.entity.CustomUser;
import com.zyc.file_system.entity.Region;
import com.zyc.file_system.entity.SystemUser;
import com.zyc.file_system.entity.TreeNode;
import com.zyc.file_system.repositry.RegionRepository;
import com.zyc.file_system.repositry.SystemUserRepository;
import com.zyc.file_system.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;


    @Autowired
    private SystemUserRepository systemUserRepository;

    @Override
    public Region findByCode(String code) {
        return regionRepository.findByCodeAndDel(code,0);
    }

    @Override
    public synchronized Region save(Region region) {


        //检查code的唯一性
        Region previous = findByCode(region.getCode());
        if(previous != null){
            throw new RuntimeException("区域编码已存在，请勿重复");
        }

        region = regionRepository.save(region);
        if(!"0".equals(region.getParentId())){//顶层节点
            Region parentRegion = regionRepository.findById(region.getParentId()).get();
            if(parentRegion == null){
                throw new RuntimeException("父节点不存在");
            }
            region.setPath(parentRegion.getPath()+"/"+region.getId());
        }else{
            region.setPath("/"+region.getId());
        }

        return regionRepository.save(region);


    }

    @Override
    public synchronized Region update(Region region) {
        Region regionCondition = new Region();
        regionCondition.setId(region.getId());
        if(!regionRepository.exists(Example.of(regionCondition))){
            throw new RuntimeException("id不存在");
        }

        //检查code的唯一性
        Region previous = regionRepository.findByCodeAndDelAndIdNot(region.getCode(),0,region.getId());
        if(previous != null){
            throw new RuntimeException("区域编码已存在，请勿重复");
        }



        if(!"0".equals(region.getParentId())){//顶层节点
            Region parentRegion = regionRepository.findById(region.getParentId()).get();
            if(parentRegion == null){
                throw new RuntimeException("父节点不存在");
            }
            region.setPath(parentRegion.getPath()+"/"+region.getId());
        }else{
            region.setPath("/"+region.getId());
        }
        return regionRepository.save(region);
    }

    @Override
    public Region findById(String id) {
        return regionRepository.findById(id).get();
    }


    public List<TreeNode> tree() {
        //先查出当前用户管理的机构id
//        List<Region> regions = regionRepository.findByPathLikeAndDel(region.getPath(),0);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("del", ExampleMatcher.GenericPropertyMatchers.caseSensitive());

        Region region = new Region();
        region.setDel(0);

        Example<Region> example = Example.of(region ,matcher);
        List<Region> regions = regionRepository.findAll(example);
        List<TreeNode> nodes = getTreeNodes(regions);

        return  nodes;

    }

    private List<TreeNode> getTreeNodes(List<Region> regions) {
        Map<String,TreeNode> regionMap = new HashMap<>();
        regions.forEach(r -> {
            TreeNode treeNode = new TreeNode();
            treeNode.setCode(r.getCode());
            treeNode.setId(r.getId());
            treeNode.setPath(r.getPath());
            treeNode.setTitle(r.getName());


            regionMap.put(r.getId(),treeNode);
        });

        List<TreeNode> nodes = new ArrayList<>();
        regions.forEach(r -> {
            String parentId = r.getParentId();

            TreeNode node = regionMap.get(r.getId());
            TreeNode parentNode = regionMap.get(r.getParentId());
            if(parentNode==null){

                nodes.add(node);

            }else{
                List<TreeNode> children = parentNode.getChildren();
                if(children == null){
                    children = new ArrayList<>();
                    parentNode.setChildren(children);
                }
                children.add(node);

            }


        });
        return nodes;
    }

    @Override
    public List<TreeNode> authtree() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) auth.getPrincipal();
        String regionId = user.getRegionId();
        if(StringUtils.isEmpty(regionId)){
            return null;
        }
        Region region = regionRepository.findById(regionId).get();
        if(region == null){
            return null;
        }


        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("del", ExampleMatcher.GenericPropertyMatchers.caseSensitive())
                .withMatcher("path",ExampleMatcher.GenericPropertyMatchers.startsWith());

        Region regionCondition = new Region();
        regionCondition.setDel(0);
        regionCondition.setPath(region.getPath());

        Example<Region> example = Example.of(regionCondition ,matcher);
        List<Region> regions = regionRepository.findAll(example);

        List<TreeNode> nodes = getTreeNodes(regions);
        return  nodes;
    }

    @Override
    public void delete(String id) {

        int count = systemUserRepository.countByRegionId(id);
        if(count > 0){
            throw new RuntimeException("该区域存在绑定用户，无法删除");
        }
        Region region = regionRepository.findById(id).get();
        if(region == null){
            throw new RuntimeException("区域不存在");
        }


        //检查是否存在子区域
        Specification<Region> s1 = new Specification<Region>() {
            @Override
            public Predicate toPredicate(Root<Region> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p1 = criteriaBuilder.notEqual(root.get("id"),id);
                Predicate p2 = criteriaBuilder.like(root.get("path"),region.getPath()+"%");
                Predicate p3 = criteriaBuilder.equal(root.get("del"),0);
                return criteriaBuilder.and(p1,p2,p3);
            }
        };


        long regionsCount = regionRepository.count(s1);
        if(regionsCount > 0){
            throw new RuntimeException("当前区域还存在有效的子区域，无法删除");
        }

        region.setDel(1);
        regionRepository.save(region);
    }

    /**
     * 检查是否有操作某个区域的权限
     * @return
     */
    @Override
    public boolean checkPermission(String aim) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) auth.getPrincipal();
        String regionId = user.getRegionId();
        if(regionId == null){
            return false;
        }

        Specification<Region> s1 = new Specification<Region>() {
            @Override
            public Predicate toPredicate(Root<Region> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p1 = criteriaBuilder.equal(root.get("id"),aim);
                Predicate p2 = criteriaBuilder.like(root.get("path"),"%"+regionId+"%");
                Predicate p3 = criteriaBuilder.equal(root.get("del"),0);
                return criteriaBuilder.and(p1,p2,p3);
            }
        };
        if(regionRepository.count(s1)>0){
            return true;
        }




        return false;
    }


}
