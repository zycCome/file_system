package com.zyc.file_system.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 地区表
 */
@Table(name="tb_region")
@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "org.hibernate.id.UUIDGenerator" )
@Data
public class Region {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;

    @NotBlank(message = "区域名称不能为空")
    private String name;//区域名称

    @NotBlank(message = "区域编码不能为空")
    private String code;//地区码（就是前端传的region字段）

    private String path;

    private String parentId;//父节点id.如果是顶层节点，则传0

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp()
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @UpdateTimestamp
    private Date updateTime;

    private Integer del = 0;//是否删除，1表示删除

}
