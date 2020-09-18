package com.ds.feige.im.oss.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * 文件
 *
 * @author DC
 */
@TableName("t_oss_file")
@Data
public class OSSFile extends BaseEntity {
    private String fileName;
    private String domain;
    private String url;
    private String scene;
    private String md5;
    private Long size;
    private Integer mtime;
    private Long uploadUserId;
    private String uploadChannel;
}
