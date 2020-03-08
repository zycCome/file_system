package com.zyc.file_system.controller;

import com.zyc.file_system.config.FileBaseConfiguration;
import com.zyc.file_system.entity.CustomUser;
import com.zyc.file_system.entity.TreeNode;
import com.zyc.file_system.entity.WebResult;
import com.zyc.file_system.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;


    private FileBaseConfiguration fileBaseConfiguration;

    private Pattern pattern;

    @Autowired
    public void setFileBaseConfiguration(FileBaseConfiguration fileBaseConfiguration) {
        this.fileBaseConfiguration = fileBaseConfiguration;
        pattern = Pattern.compile(fileBaseConfiguration.getRegex());
    }

    @GetMapping("/tree")
    public WebResult getTree(){
        File dir=new File(fileBaseConfiguration.getPath()+File.separator+fileBaseConfiguration.getFolder());
        TreeNode treeNode = new TreeNode();
        treeNode.setTitle(dir.getName());
        treeNode.setPath(File.separator+dir.getName());

        List<TreeNode> treeNodes = new ArrayList<>();//用户能看到目录节点
        List<TreeNode> allTreeNode = new ArrayList<>();//所有的目录的目录节点。里面有父子关系

        FileService.listDirectory(dir,treeNode,allTreeNode);


        /**
         * 需要根据用户角色或者授权目录对完整的目录树进行过滤
         */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) auth.getPrincipal();
        for (GrantedAuthority authority : user.getAuthorities()) {
            String authorityString = authority.getAuthority();
            if(authorityString.equals("ROLE_SUPERADMIN")){//超级用户不看目录权限
                treeNodes.add(treeNode);
                return WebResult.success(treeNodes);//最大的目录
            }
        }

        /*
        这里如果用户的授权目录中有父子目录的话，那就会有重复的树。所以在授权接口中，做了授权目录大的覆盖小的处理
         */
        for (String directory : user.getAuthorizedDirectory()) {
            for (TreeNode node : allTreeNode) {
                if(node.getPath().equals(directory)){
                    treeNodes.add(node);
                }
            }

        }


        return WebResult.success(treeNodes);
    }


    @GetMapping("/list")
    @PreAuthorize("hasPermission(#directory,'/list')")
    public WebResult listDirectory(String directory){
        return WebResult.success(fileService.listImages(directory,pattern));
    }


    @PostMapping("/save")
    @PreAuthorize("hasPermission(#directory,'/save')")
    public WebResult save(MultipartFile imageFile,String directory,String filename) throws IOException {
        if (imageFile == null||imageFile.isEmpty()) {
            return WebResult.fail("文件不能为空");
        }

        Matcher matcher = pattern.matcher(filename);
        if(!matcher.find()){
            return  WebResult.fail("filename异常");
        }

        String filePath = fileBaseConfiguration.getPath()+File.separator+directory;
        File dest = new File(filePath + File.separator+filename);


        if(!isImage(imageFile.getInputStream())){
            return  WebResult.fail("文件类型异常");
        }

        try {
            imageFile.transferTo(dest);

            return WebResult.success();
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
        return  WebResult.fail("文件上传失败");

    }


    @DeleteMapping("/delete")
    @PreAuthorize("hasPermission(#path,'/delete')")
    public WebResult delete(String path){
        String filePath = fileBaseConfiguration.getPath()+File.separator+path;
        File dest = new File(filePath);

        Matcher matcher = pattern.matcher(path);
        if(!matcher.find()){
            return  WebResult.fail("filename异常");
        }

        if(!dest.exists()){
            return  WebResult.fail("照片不存在");
        }else{

            if(dest.delete()){
                return  WebResult.success();
            }else{
                return  WebResult.fail("照片删除异常");
            }

        }


    }


    /**
     * 判断文件是否是图片
     * @param inputStream
     * @return
     */
    private boolean isImage(InputStream inputStream) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/showImage")
    @PreAuthorize("hasPermission(#path,'/showImage')")
    public void showImage(String path, HttpServletResponse response){
        byte[] data = null;
        String filePath = fileBaseConfiguration.getPath()+File.separator+path;
        File dest = new File(filePath);
        if(dest.exists()){
            String ext = dest.getName().substring(dest.getName().lastIndexOf(".")+1);
            response.setCharacterEncoding("utf-8");
            response.setContentType("image/"+ext);
            response.setContentLength((int) dest.length());
            data = new byte[(int)dest.length()];
            response.setHeader("Cache-Control", "no-cache"); //设置缓存

            try(OutputStream os = response.getOutputStream();
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dest));){
                int i = 0;
                while ((i = bis.read(data)) != -1) {
                    os.write(data, 0, i);
                    os.flush();
                }

                response.setStatus(HttpServletResponse.SC_OK);
                return;
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

    }


}
