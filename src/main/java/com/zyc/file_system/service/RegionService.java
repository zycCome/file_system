package com.zyc.file_system.service;

import com.zyc.file_system.entity.Region;
import com.zyc.file_system.entity.TreeNode;

import java.util.List;

public interface RegionService {

    Region findByCode(String code);

    Region save(Region region);

    Region update(Region region);

    Region findById(String id);

    List<TreeNode> tree();

    List<TreeNode> authtree();

    void delete(String id);

    public boolean checkPermission(String regionId);

}
