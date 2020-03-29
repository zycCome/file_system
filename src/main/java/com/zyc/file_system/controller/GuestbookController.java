package com.zyc.file_system.controller;


import com.zyc.file_system.entity.*;
import com.zyc.file_system.service.GuestbookService;
import com.zyc.file_system.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/guestbook")
@RestController
public class GuestbookController {

    @Autowired
    private GuestbookService guestbookService;


    @Autowired
    private RegionService regionService;


    /**
     * 前台用户不用权限
     * @param guestbook
     * @return
     */
    @PostMapping()
    public WebResult save(Guestbook guestbook, BindingResult bindingResult){

        guestbook.setStatus(0);
        guestbook.setReply("");
        guestbook.setUpdateUser("");

        Region region = regionService.findByCode(guestbook.getRegion());
        if(region!= null){
            guestbook.setRegionName(region.getName());
        }

        return WebResult.success(guestbookService.save(guestbook));
    }


    @GetMapping("/page")
    public WebResult page(int page,int size,String regionId){
        if(StringUtils.isEmpty(regionId)){
            //默认就是当前用户所属的区域
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUser user = (CustomUser) auth.getPrincipal();
            regionId = user.getRegionId();
        }else if(!regionService.checkPermission(regionId)){
            return WebResult.fail("暂无查看此区域的权限");

        }

        Sort sort = Sort.by(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Guestbook> page_guestbook= guestbookService.findAll(pageable,regionId);
        return WebResult.success(page_guestbook);
    }



    @PostMapping("/audit/{id}")
    public WebResult audit(@PathVariable String id, String reply, int status){
        Guestbook guestbook = guestbookService.findById(id);
        if(guestbook == null){
            return WebResult.fail("记录不存在");
        }
        if(guestbook.getStatus() > 0){
            return WebResult.fail("操作异常");
        }
        if(status == 2 && StringUtils.isEmpty(reply)){
            return WebResult.fail("展示时,回复不能为空");
        }

        Region region = regionService.findByCode(guestbook.getRegion());
        if(region != null && !regionService.checkPermission(region.getId())){
            return WebResult.fail("没有操作此记录的权限");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) auth.getPrincipal();



        guestbook.setUpdateUser(user.getUsername());
        guestbook.setStatus(status);
        guestbook.setReply(reply);
        return WebResult.success(guestbookService.save(guestbook));


    }

    @DeleteMapping("/{id}")
    public WebResult delete(@PathVariable String id){
        Guestbook guestbook = guestbookService.findById(id);


        Region region = regionService.findByCode(guestbook.getRegion());
        if(region != null && !regionService.checkPermission(region.getId())){
            return WebResult.fail("没有操作此记录的权限");
        }
        guestbook.setDel(1);

        return WebResult.success(guestbookService.save(guestbook));
    }


    /**
     * 公开展示的留言
     * @param page
     * @param size
     * @param code 区域编码
     * @return
     */
    @GetMapping("/open/page")
    public WebResult opnePage(int page,int size,@RequestParam(required = true) String code){

        Sort sort = Sort.by(Sort.Direction.DESC,"createTime"); //创建时间降序排序
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Guestbook> page_guestbook= guestbookService.findAllByCode(pageable,code);
        return WebResult.success(page_guestbook);
    }




}
