package com.ds.feige.im.oss.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

/**
 * 文件
 *
 * @author DC
 */
@TableName("t_oss_file")
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Integer getMtime() {
        return mtime;
    }

    public void setMtime(Integer mtime) {
        this.mtime = mtime;
    }

    public Long getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(Long uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public String getUploadChannel() {
        return uploadChannel;
    }

    public void setUploadChannel(String uploadChannel) {
        this.uploadChannel = uploadChannel;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "OSSFile{" +
                "fileName='" + fileName + '\'' +
                ", domain='" + domain + '\'' +
                ", url='" + url + '\'' +
                ", scene='" + scene + '\'' +
                ", md5='" + md5 + '\'' +
                ", size=" + size +
                ", mtTime=" + mtime +
                ", uploadUserId=" + uploadUserId +
                ", uploadChannel='" + uploadChannel + '\'' +
                '}';
    }
}
