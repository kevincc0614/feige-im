package com.ds.feige.im.oss.service;

import com.ds.feige.im.oss.dto.UploadCompleteRequest;

/**
 * 对象存储服务
 *
 * @author DC
 */
public interface OSSService {

    long uploadComplete(UploadCompleteRequest request);
}
