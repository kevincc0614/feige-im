package com.ds.feige.im.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.app.dto.AppInfo;
import com.ds.feige.im.app.service.AppService;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.enterprise.dto.CreateAppRequest;
import com.ds.feige.im.enterprise.service.EnterpriseService;

/**
 * @author DC 后台管理相关
 */
@RestController
@RequestMapping("/admin/app")
public class AppAdminController {
    @Autowired
    AppService appService;
    @Autowired
    EnterpriseService enterpriseService;

    @PostMapping("/create")
    public Response createApp(HttpServletRequest request, @RequestBody @Valid CreateAppRequest createApp) {
        createApp.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(createApp.getEnterpriseId(), createApp.getOperatorId());
        AppInfo appInfo = appService.createApp(createApp);
        return new Response(appInfo);
    }
}
