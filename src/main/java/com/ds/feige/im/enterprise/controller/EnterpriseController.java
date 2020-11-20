package com.ds.feige.im.enterprise.controller;

import java.util.List;
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
        EmployeeInfo employeeInfo = enterpriseService.getEmp(enterpriseId, userId);
        List<RoleAuthorityInfo> authorities = enterpriseSecurityService.getEmpAuthorities(enterpriseId, userId);
        Set<Long> appIds = Sets.newHashSet();
        if (authorities != null && !authorities.isEmpty()) {
            authorities.forEach(a -> appIds.add(a.getAppId()));
        }
        List<AppInfo> appInfos = appService.getApps(appIds);
        EmpProfile profile = new EmpProfile();
        profile.setEmployee(employeeInfo);
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
    Response<EmployeeInfo> getEmployeeInfo(@RequestBody GetEmpRequest request) {
        EmployeeInfo employeeInfo = enterpriseService.getEmp(request.getEnterpriseId(), request.getUserId());
        return new Response<>(employeeInfo);
    }

    @RequestMapping("/departments")
    Response<List<DepartmentBaseInfo>> getDepartments(long enterpriseId) {
        List<DepartmentBaseInfo> infos = enterpriseService.getDepartments(enterpriseId);
        return new Response<>(infos);
    }
}
