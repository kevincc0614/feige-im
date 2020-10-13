package com.ds.feige.im.app.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.app.dto.*;
import com.ds.feige.im.app.service.AppSecurityService;
import com.ds.feige.im.app.service.AppService;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.enterprise.dto.EnterpriseOpRequest;
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
    @Autowired
    AppSecurityService appSecurityService;

    @PostMapping("/create")
    public Response createApp(HttpServletRequest request, @RequestBody @Valid CreateAppRequest createApp) {
        createApp.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(createApp.getEnterpriseId(), createApp.getOperatorId());
        AppInfo appInfo = appService.createApp(createApp.getEnterpriseId(), createApp.getName(), createApp.getAvatar());
        return new Response(appInfo);
    }

    @PostMapping("/security/emp-role/create")
    public Response createEmpRole(HttpServletRequest request, @RequestBody @Valid CreateEmpRoleRequest createEmpRole) {
        createEmpRole.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(createEmpRole.getEnterpriseId(), createEmpRole.getOperatorId());
        long roleId = appSecurityService.createEmpRole(createEmpRole);
        return new Response(roleId);
    }

    @PostMapping("/security/emp-role/delete")
    public Response deleteEmpRole(HttpServletRequest request, @RequestBody @Valid DeleteEmpRoleRequest deleteEmp) {
        deleteEmp.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(deleteEmp.getEnterpriseId(), deleteEmp.getOperatorId());
        appSecurityService.deleteEmpRole(deleteEmp);
        return Response.EMPTY_SUCCESS;
    }

    @PostMapping("/security/emp-role/list")
    public Response empRoleList(HttpServletRequest request, @RequestBody @Valid EnterpriseOpRequest query) {
        query.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(query.getEnterpriseId(), query.getOperatorId());
        List<EmpRoleInfo> infos = appSecurityService.getEnterpriseRoles(query);
        return new Response(infos);
    }

    @PostMapping("/security/role-authority/create")
    public Response createRoleAuthority(HttpServletRequest request,
        @RequestBody @Valid CreateRoleAuthority createRoleAuthority) {
        createRoleAuthority.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(createRoleAuthority.getEnterpriseId(), createRoleAuthority.getOperatorId());
        long authorityId = appSecurityService.createRoleAuthority(createRoleAuthority);
        return new Response(authorityId);
    }

    @PostMapping("/security/role-authority/list")
    public Response getRoleAuthorities(HttpServletRequest request, RoleAuthoritiesQuery query) {
        query.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(query.getEnterpriseId(), query.getOperatorId());
        List<RoleAuthorityInfo> authorities = appSecurityService.getRoleAuthorities(query.getRoleId());
        return new Response(authorities);
    }

    @PostMapping("/security/emp-role-binding/edit")
    public Response bindEmpRole(HttpServletRequest request, @RequestBody @Valid EditEmpRoleBindingRequest empRole) {
        empRole.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(empRole.getEnterpriseId(), empRole.getOperatorId());
        appSecurityService.editEmpRoleBindings(empRole);
        return Response.EMPTY_SUCCESS;
    }
}
