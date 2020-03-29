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


/**
 * 留言板
 */
@Table(name="tb_guestbook")
@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "org.hibernate.id.UUIDGenerator" )
@Data
public class Guestbook {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;

    private String name;

    private String phone;

    private Integer open = 1;//是否公开，0表示不公开，1表示公开。提交者确定

    private Integer status;//留言状态。0表示未处理，1表示已处理但不展示，2表示已处理并展示

    private String regionName;//机构名称。后台会根据region字段查询

    private String reply;//回复

    private String updateUser;//回复的管理员用户名


    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp()
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @UpdateTimestamp
    private Date updateTime;

    private String content;//留言内容


    @NotBlank(message = "所属区域不能为空")
    private String region;//机构的code

    private Integer del = 0;//1表示删除




}
