package com.ds.feige.im.oss.dto;

import lombok.Data;

/**
 * 上传凭证结果
 *
 * @author DC
 */
@Data
public class UploadTokenResult {
    private String uploadUrl;
    private String uploadToken;
}
