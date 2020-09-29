package com.ds.feige.im.oss.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 上传成功通知
 *
 * @author DC
 */
@Data
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
    @Positive
    private Long uploadUserId;
    @NotBlank
    private String uploadChannel;
}
