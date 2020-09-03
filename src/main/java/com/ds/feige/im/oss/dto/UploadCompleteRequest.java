package com.ds.feige.im.oss.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * 上传成功通知
 *
 * @author DC
 */
public class UploadCompleteRequest {
    @Size(min = 8, max = 100)
    private String domain;
    @Size(min = 1, max = 200)
    private String fileName;
    @Size(min = 1, max = 45)
    private String md5;
    @Positive
    private long size;
    @Positive
    private Integer mtime;
    @NotBlank
    private String scene;
    @Size(min = 8, max = 500)
    private String url;
    private Long uploadUserId;
    private String uploadChannel;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
