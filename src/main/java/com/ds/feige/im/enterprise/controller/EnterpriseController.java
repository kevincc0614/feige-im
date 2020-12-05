package com.ds.feige.im.enterprise.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.app.dto.AppInfo;
import com.ds.feige.im.app.service.AppService;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.service.EnterpriseSecurityService;
import com.ds.feige.im.enterprise.service.EnterpriseService;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author DC
 */
@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    SessionUserService sessionUserService;
    @Autowired
    AppService appService;
    @Autowired
    EnterpriseSecurityService enterpriseSecurityService;

    @RequestMapping("/employee/profile")
    public Response<EmpProfile> getEmployeeProfile(HttpServletRequest request, long enterpriseId) {
        long userId = WebUtils.getUserId(request);
        EmpDetails empDetails = enterpriseService.getEmpDetails(enterpriseId, userId);
        List<RoleAuthorityInfo> authorities = enterpriseSecurityService.getEmpAuthorities(enterpriseId, userId);
        Set<Long> appIds = Sets.newHashSet();
        if (authorities != null && !authorities.isEmpty()) {
            authorities.forEach(a -> appIds.add(a.getAppId()));
        }
        List<AppInfo> appInfos = appService.getApps(appIds);
        EmpProfile profile = new EmpProfile();
        profile.setEmployee(empDetails);
        profile.setApps(appInfos);
        return new Response(profile);
    }
    @RequestMapping("/list")
    public Response<List<EnterpriseInfo>> getEnterprises(HttpServletRequest request) {
        long userId = WebUtils.getUserId(request);
        List<EnterpriseInfo> enterpriseInfos = enterpriseService.getEnterprises(userId);
        return new Response<>(enterpriseInfos);
    }

    @RequestMapping(value = "/department")
    Response<DepartmentDetails> getDepartment(@RequestBody GetDepRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long departmentId = request.getDepartmentId();
        boolean isQueryChild = request.isQueryChild();
        DepartmentDetails departmentDetails = enterpriseService.getDepartment(enterpriseId, departmentId, isQueryChild);
        if (departmentDetails != null && departmentDetails.getEmployees() != null) {
            departmentDetails.getEmployees().forEach(e -> {
                e.setState(sessionUserService.getSessionUser(e.getUserId()).getState());
            });
        }
        return new Response<>(departmentDetails);
    }
    @RequestMapping("/employee/info")
    Response<EmpDetails> getEmployeeInfo(@RequestBody GetEmpRequest request) {
        EmpDetails empDetails = enterpriseService.getEmpDetails(request.getEnterpriseId(), request.getUserId());
        return new Response<>(empDetails);
    }

    @RequestMapping("/employees")
    public Response<List<EmpDetails>> employeeList(HttpServletRequest request, long enterpriseId) {
        // TODO 判断是否为所属企业ID
        Set<Long> exclude = new HashSet<>();
        exclude.add(WebUtils.getUserId(request));
        List<EmpDetails> overviews = enterpriseService.getAllEmpDetailList(enterpriseId, exclude);
        return new Response(overviews);
    }
    @RequestMapping("/departments")
    Response<List<DepartmentOverview>> getDepartments(long enterpriseId) {
        List<DepartmentOverview> overviews = enterpriseService.getDepartments(enterpriseId);
        Map<Long, DepartmentOverview> depMap = Maps.newHashMap();
        overviews.forEach(overview -> depMap.put(overview.getId(), overview));
        return new Response<>(overviews);
    }
}
