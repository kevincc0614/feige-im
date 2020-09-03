package com.ds.feige.im.oss.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.oss.configurer.OSSConfigProperties;
import com.ds.feige.im.oss.dto.UploadCompleteRequest;
import com.ds.feige.im.oss.entity.OSSFile;
import com.ds.feige.im.oss.mapper.OSSFileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 对象存储服务GoFastDfs实现
 *
 * @author DC
 */
@Service
public class OSSServiceImpl extends ServiceImpl<OSSFileMapper, OSSFile> implements OSSService {
    OSSConfigProperties configProperties;
    static final Logger LOGGER = LoggerFactory.getLogger(OSSServiceImpl.class);

    @Autowired
    public OSSServiceImpl(OSSConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public long uploadComplete(UploadCompleteRequest request) {
        OSSFile file = BeansConverter.uploadCompleteToOSSFile(request);
        save(file);
        LOGGER.info("File upload complete:file={}", file);
        return file.getId();
    }
}
