package com.zyc.file_system.controller;

import com.zyc.file_system.entity.Region;
import com.zyc.file_system.entity.SystemUser;
import com.zyc.file_system.entity.WebResult;
import com.zyc.file_system.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping("/region")
public class RegionController {

    @Autowired
    private RegionService regionService;

    /**
     * 保存区域
     * 需要校验父id，维护path
     * code唯一
     * @return
     */
    @PostMapping("")
    @RolesAllowed("SUPERADMIN")
    public WebResult save(@Valid @RequestBody Region region, BindingResult bindingResult){
        return WebResult.success(regionService.save(region));
    }

    @PutMapping("/{id}")
    @RolesAllowed("SUPERADMIN")
    public WebResult update(@PathVariable String id,@Valid @RequestBody Region region, BindingResult bindingResult){
        region.setId(id);
        return WebResult.success(regionService.update(region));
    }

    @GetMapping("/tree")
    @RolesAllowed("SUPERADMIN")
    public WebResult tree(){

        return WebResult.success(regionService.tree());
    }

    @GetMapping("/authtree")
    public WebResult authtree(){

        return WebResult.success(regionService.authtree());
    }


    @DeleteMapping("/{id}")
    @RolesAllowed("SUPERADMIN")
    public WebResult delete(@PathVariable String id){
        regionService.delete(id);
        return WebResult.success();

    }



}
