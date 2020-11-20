package com.ds.feige.im.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ds.base.nodepencies.api.Response;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.app.dto.AppInfo;
import com.ds.feige.im.app.dto.AppOperationRequest;
import com.ds.feige.im.app.service.AppService;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.dto.PermissionCheckRequest;
import com.ds.feige.im.enterprise.service.EnterpriseSecurityService;

import cn.hutool.core.codec.Base64;

/**
 * @author DC 开放API相关
 */
@RestController
@RequestMapping("/api/app")
public class AppApiController {
    @Autowired
    EnterpriseSecurityService enterpriseSecurityService;
    @Autowired
    AppService appService;
    @Autowired
    UserService userService;

    @GetMapping("/get-token")
    public Response<String> getAccessToken(long enterpriseId, String secret) {
        String accessToken = appService.getAccessToken(enterpriseId, secret);
        return new Response<>(accessToken);
    }

    @PostMapping("/security/check-permission")
    public Response checkPermission(String accessToken, @RequestBody AppOperationRequest request) {
        java.lang.String openId = request.getUserOpenId();
        String base64OpenId = Base64.decodeStr(openId);
        String[] idArray = base64OpenId.split(",");
        long userId = Long.valueOf(idArray[0]);
        long appId = Long.valueOf(idArray[1]);
        AppInfo appInfo = appService.verifyToken(accessToken);
        if (appInfo.getId() != appId) {
            throw new WarnMessageException(FeigeWarn.USER_OPENID_INVALID);
        }
        UserInfo userInfo = userService.getUserById(userId);
        if (userInfo == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        PermissionCheckRequest checkRequest = new PermissionCheckRequest();
        checkRequest.setEnterpriseId(appInfo.getEnterpriseId());
        checkRequest.setAppId(appInfo.getId());
        checkRequest.setUserId(userInfo.getUserId());
        checkRequest.setResource(request.getResource());
        checkRequest.setMethod(request.getMethod());
        boolean result = enterpriseSecurityService.checkPermission(checkRequest);
        return new Response(result);
    }

}
