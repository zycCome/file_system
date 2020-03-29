package com.zyc.file_system.entity;

import lombok.Data;

import java.util.List;


@Data
public class TreeNode {

    private String title;

    private String path;

    private boolean  expand = true;

    private List<TreeNode> children;

    private String id;

    private String code;
}
