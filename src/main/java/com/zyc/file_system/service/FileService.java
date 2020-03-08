package com.zyc.file_system.service;


import com.zyc.file_system.entity.ImageFile;
import com.zyc.file_system.entity.TreeNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public interface FileService {

    /**
     *  @param dir
     * @param pnode
     * @param allTreeNode
     */
    static void listDirectory(File dir, TreeNode pnode, List<TreeNode> allTreeNode) {
        allTreeNode.add(pnode);//深度遍历，每个目录只会加入一次
        File[] files=dir.listFiles();
        for(int i=0;i<files.length;i++){
            if(files[i].isDirectory()){
                //还是目录，继续遍历
                TreeNode treeNode = new TreeNode();
                treeNode.setTitle(files[i].getName());
                treeNode.setPath(pnode.getPath()+File.separator+files[i].getName());
                List<TreeNode> children =  pnode.getChildren();
                if(children == null){
                    children = new ArrayList<>();
                    pnode.setChildren(children);
                }
                children.add(treeNode);
                listDirectory(files[i],treeNode, allTreeNode);
            }

        }

    }

    public List<ImageFile> listImages(String directory, Pattern pattern);



    /**
     * 获取文件大小
     */
    public String getFileLength(long size);

}
