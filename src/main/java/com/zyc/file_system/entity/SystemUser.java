package com.zyc.file_system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Table(name="tb_system_user")
@Entity()
//@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@GenericGenerator(name = "jpa-uuid", strategy = "org.hibernate.id.UUIDGenerator" )
@Data
public class SystemUser {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;

    @NotBlank(message = "用户名不能为空")
    @Column(unique = true,updatable = false)
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp()
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @UpdateTimestamp
    private Date updateTime;

    private Boolean enable = true;

    @NotBlank(message = "角色不能为空")
    private String role;//只有两种角色，SUPERADMIN和USER


    @ElementCollection(fetch=FetchType.LAZY, //加载策略,延迟加载
            targetClass=String.class) //指定集合中元素的类型
    @CollectionTable(name="tb_authorized_directory")
    @OrderColumn(name="order_num")
    private List<String> authorizedDirectory = new ArrayList<>();



}
