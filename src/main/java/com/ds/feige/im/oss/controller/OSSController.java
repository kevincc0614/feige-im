package com.ds.feige.im.oss.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.controller.NodAuthorizedRequest;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.oss.configurer.OSSConfigProperties;
import com.ds.feige.im.oss.dto.UploadCompleteRequest;
import com.ds.feige.im.oss.dto.UploadTokenResult;
import com.ds.feige.im.oss.service.OSSService;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

import lombok.extern.slf4j.Slf4j;

/**
 * OSS
 *
 * @author DC
 */
@Controller
@RequestMapping("/oss")
@Slf4j
public class OSSController {
    @Autowired
    OSSConfigProperties configProperties;
    @Autowired
    OSSService ossService;
    private static final Logger LOGGER = LoggerFactory.getLogger(OSSController.class);

    /**
     * TODO 可以考虑更加严格的校验,比对文件名之类的相关信息
     */
    @RequestMapping("/upload/verify-token")
    @NodAuthorizedRequest
    @ResponseBody
    public String verifyToken(HttpServletRequest request, @RequestParam(value = "auth_token") String token)
        throws Exception {
        log.info("Verify upload token:token={}", token);
        if (Strings.isNullOrEmpty(token)) {
            return "token is empty";
        }
        String[] tokenArray = token.split(":");
        String sign = tokenArray[0];
        String uploadPolicy = tokenArray[1];
        String exceptSign = Hashing.hmacSha1(configProperties.getTokenSecret().getBytes()).newHasher()
            .putString(uploadPolicy, Charsets.UTF_8).hash().toString();
        if (!exceptSign.equals(sign)) {
            LOGGER.error("sign is invalid:token={}", token);
            return "sign is invalid";
        }
        // 检查过期时间
        String uploadPolicyBase64Decode = new String(Base64.getDecoder().decode(uploadPolicy.getBytes()));
        String uploadPolicyURLDecode = URLDecoder.decode(uploadPolicyBase64Decode, "UTF-8");
        HashMap map = JsonUtils.jsonToBean(uploadPolicyURLDecode, HashMap.class);
        Long expiredAt = (Long)map.get("expiredAt");
        if (expiredAt == null) {
            LOGGER.error("token is invalid,has no expiredAt property:token={}", token);
            return "token is invalid,has no expiredAt property";
        }
        if (expiredAt <= System.currentTimeMillis()) {
            LOGGER.error("token is expired:token={}", token);
            return "expired";
        }
        return "ok";
    }

    /**
     * @param requestBody fileName 文件名 scene 场景  size 文件大小
     */
    @RequestMapping("/upload/get-token")
    @ResponseBody
    public Response<UploadTokenResult> getUploadToken(@RequestBody Map<String, Object> requestBody) throws Exception {
        Instant expiredAt = Instant.now().plusSeconds(configProperties.getTokenExpireSeconds());
        requestBody.put("expiredAt", expiredAt.toEpochMilli());
        String bodyJson = JsonUtils.toJson(requestBody);
        String urlEncode = URLEncoder.encode(bodyJson, "UTF-8");
        String uploadPolicy = Base64.getEncoder().encodeToString(urlEncode.getBytes());
        String sign = Hashing.hmacSha1(configProperties.getTokenSecret().getBytes()).newHasher().putString(uploadPolicy, Charsets.UTF_8).hash().toString();
        String token = sign + ":" + uploadPolicy;
        UploadTokenResult result = new UploadTokenResult();
        result.setUploadToken(token);
        result.setUploadUrl(configProperties.getDomain() + "/" + configProperties.getGroup() + "/upload");
        return new Response<>(result);
    }

    @RequestMapping("/upload/complete")
    @ResponseBody
    public Response uploadComplete(HttpServletRequest servletRequest,
        @RequestBody @Valid UploadCompleteRequest request) {
        request.setUploadUserId(WebUtils.getUserId(servletRequest));
        long fileId = ossService.uploadComplete(request);
        return new Response(fileId);
    }
}
