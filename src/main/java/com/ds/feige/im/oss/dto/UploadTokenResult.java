package com.ds.feige.im.oss.dto;

/**
 * 上传凭证结果
 *
 * @author DC
 */
public class UploadTokenResult {
    private String uploadUrl;
    private String uploadToken;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getUploadToken() {
        return uploadToken;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }
}
