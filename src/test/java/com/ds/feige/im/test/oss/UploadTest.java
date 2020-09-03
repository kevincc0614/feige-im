package com.ds.feige.im.test.oss;

import cn.hutool.http.HttpUtil;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.oss.dto.UploadCompleteRequest;
import com.ds.feige.im.oss.service.OSSService;
import com.ds.feige.im.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传测试
 *
 * @author DC
 */
public class UploadTest extends BaseTest {
    @Autowired
    OSSService ossService;

    @Test
    public void testUpload() throws Exception {
        //文件地址
        File file = new File("/Users/caedmon/Desktop/pics/test-1.jpg");
        //声明参数集合
        HashMap<String, Object> paramMap = new HashMap<>();
        //文件
        paramMap.put("file", file);
        //输出
        paramMap.put("output", "json");
//        //自定义路径
//        paramMap.put("path", "avatar");
        //场景
        paramMap.put("scene", "avatar");
        paramMap.put("auth_token", "test-token");
        //上传
        String result = HttpUtil.post("http://35.220.161.103:8080/group1/upload", paramMap);
        System.out.println(result);
        //输出json结果
        Map<String, Object> response = JsonUtils.jsonToBean(result, HashMap.class);
        UploadCompleteRequest request = new UploadCompleteRequest();
        request.setDomain((String) response.get("domain"));
        request.setFileName(file.getName());
        request.setUrl((String) response.get("url"));
        request.setMd5("test-md5");
        request.setMtime((int) (System.currentTimeMillis() / 1000));
        request.setSize(file.length());
        request.setUploadChannel("IM-APP");
        request.setScene("avatar");
        long fileId = ossService.uploadComplete(request);
        System.out.println(fileId);
    }

    @Test
    public void testDecode() throws Exception {
        String s = "JTdCJTIyZmlsZU5hbWUlMjIlM0ElMjIxLnR4dCUyMiUyQyUyMmhhc2glMjIlM0ElMjJqbGFkamZsamRqZmxkc2psZiUyMiUyQyUyMnNjZW5lJTIyJTNBJTIyYXZhdGFyJTIyJTJDJTIyc2l6ZSUyMiUzQTEwMjQlN0Q=";
        String uploadPolicy = new String(Base64.getDecoder().decode(s.getBytes()));
        String decode = URLDecoder.decode(uploadPolicy, "UTF-8");
        System.out.println(decode);
    }
}
