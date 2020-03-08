package com.zyc.file_system.util;

public class DirectoryUtil {

    /**
     * 判断children是否是parent的子目录
     * @param children
     * @param parent
     * @return
     */
    public static boolean isChildrenDirectory(String children,String parent){
        if(children.startsWith(parent)){//需要处理一种情况\images和\images2是平级目录。所以必须是\images\xx才算子目录
            int length = parent.length();
            String index_later_character = children.substring(length,length+1);
            if(index_later_character.equals("\\") ||index_later_character.equals("/"))//说明这是目录
                return true;
        }

        return false;
    }

}
