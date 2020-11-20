package com.ds.feige.im.enterprise.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.service.EnterpriseSecurityService;
import com.ds.feige.im.enterprise.service.EnterpriseService;

/**
 * @author DC
 */
@RestController
@RequestMapping("/admin/security")
public class EnterpriseSecurityController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EnterpriseSecurityService enterpriseSecurityService;

    @PostMapping("/emp-role/edit")
    public Response createEmpRole(HttpServletRequest request, @RequestBody @Valid EditEmpRoleRequest createEmpRole) {
        createEmpRole.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(createEmpRole.getEnterpriseId(), createEmpRole.getOperatorId());
        long roleId = enterpriseSecurityService.editEmpRole(createEmpRole);
        return new Response(roleId);
    }

    @PostMapping("/emp-role/delete")
    public Response deleteEmpRole(HttpServletRequest request, @RequestBody @Valid DeleteEmpRoleRequest deleteEmp) {
        deleteEmp.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(deleteEmp.getEnterpriseId(), deleteEmp.getOperatorId());
        enterpriseSecurityService.deleteEmpRole(deleteEmp);
        return Response.EMPTY_SUCCESS;
    }

    @PostMapping("/emp-role/list")
    public Response empRoleList(HttpServletRequest request, @RequestBody @Valid EnterpriseOpRequest query) {
        query.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(query.getEnterpriseId(), query.getOperatorId());
        List<EmpRoleInfo> infos = enterpriseSecurityService.getEnterpriseRoles(query);
        return new Response(infos);
    }

    @PostMapping("/role-authority/create")
    public Response createRoleAuthority(HttpServletRequest request,
        @RequestBody @Valid CreateRoleAuthority createRoleAuthority) {
        createRoleAuthority.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(createRoleAuthority.getEnterpriseId(), createRoleAuthority.getOperatorId());
        long authorityId = enterpriseSecurityService.createRoleAuthority(createRoleAuthority);
        return new Response(authorityId);
    }

    @PostMapping("/role-authority/list")
    public Response getRoleAuthorities(HttpServletRequest request, @RequestBody @Valid RoleAuthoritiesQuery query) {
        query.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(query.getEnterpriseId(), query.getOperatorId());
        List<RoleAuthorityInfo> authorities = enterpriseSecurityService.getRoleAuthorities(query);
        return new Response(authorities);
    }

    @PostMapping("/emp-role-binding/edit")
    public Response bindEmpRole(HttpServletRequest request, @RequestBody @Valid EditEmpRoleBindingRequest empRole) {
        empRole.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.checkAdmin(empRole.getEnterpriseId(), empRole.getOperatorId());
        enterpriseSecurityService.editEmpRoleBindings(empRole);
        return Response.EMPTY_SUCCESS;
    }
}
