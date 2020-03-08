package com.zyc.file_system.service.impl;

import com.zyc.file_system.config.FileBaseConfiguration;
import com.zyc.file_system.entity.ImageFile;
import com.zyc.file_system.entity.TreeNode;
import com.zyc.file_system.service.FileService;
import com.zyc.file_system.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileServiceImpl implements FileService{

    @Autowired
    private FileBaseConfiguration fileBaseConfiguration;

//    public static void main(String[] args) {
//        File dir=new File("E:\\logs");
//
//        TreeNode treeNode = new TreeNode();
//        treeNode.setTitle(dir.getName());
//        FileService.listDirectory(dir,treeNode, allTreeNode);
//        System.out.println(JsonUtils.toJson(treeNode));
//    }


    public List<ImageFile> listImages(String directory, Pattern pattern){
        List<ImageFile> images = new ArrayList<>();
        File file = new File(fileBaseConfiguration.getPath()+directory);
        if(file.exists() && file.isDirectory()){
            for (File listFile : file.listFiles()) {
                if(listFile.isFile()){

                    Matcher matcher = pattern.matcher(listFile.getName());
                    if(matcher.find()){
                        images.add(new ImageFile(listFile.getName(),directory+File.separator+listFile.getName(),getFileLength(listFile.length()),new Date(listFile.lastModified())));
                    }

                }
            }
        }
        return images;
    }





    /**
     * 获取文件大小
     */
    public String getFileLength(long size){
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        }else {
            size = size / 1024;
        }

        if (size < 1024) {
            return String.valueOf(size) + "KB";
        }else {
            size = size / 1024;
        }

        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
        }else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }
}
