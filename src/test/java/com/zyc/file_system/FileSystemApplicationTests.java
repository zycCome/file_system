package com.zyc.file_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@SpringBootTest
class FileSystemApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    public void test1(){
        String reg = ".+(.JPEG|.jpeg|.JPG|.jpg)";
        String imgp= "Redocn_2012100818523401\\.jpg";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(imgp);
        System.out.println(matcher.find());
    }

}
