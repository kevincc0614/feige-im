package com.ds.feige.im.enterprise.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.service.EnterpriseService;
import com.ds.feige.im.gateway.service.SessionUserService;

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

    @RequestMapping("/list")
    public Response<List<EnterpriseInfo>> getEnterprises(HttpServletRequest request) {
        long userId = WebUtils.getUserId(request);
        List<EnterpriseInfo> enterpriseInfos = enterpriseService.getEnterprises(userId);
        return new Response<>(enterpriseInfos);
    }

    @RequestMapping(value = "/department")
    Response<DepartmentInfo> getDepartment(@RequestBody GetDepRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long departmentId = request.getDepartmentId();
        boolean isQueryChild = request.isQueryChild();
        DepartmentInfo departmentInfo = enterpriseService.getDepartment(enterpriseId, departmentId, isQueryChild);
        departmentInfo.getEmployees().forEach(e -> {
            e.setState(sessionUserService.getSessionUser(e.getUserId()).getState());
        });
        return new Response<>(departmentInfo);
    }

    @RequestMapping("/employee/info")
    Response<EmployeeInfo> getEmployeeInfo(@RequestBody GetEmpRequest request) {
        EmployeeInfo employeeInfo =
            enterpriseService.getEmployeeByUserId(request.getEnterpriseId(), request.getUserId());
        return new Response<>(employeeInfo);
    }

    @RequestMapping("/departments")
    Response<List<SimpleDepartmentInfo>> getDepartments(long enterpriseId) {
        List<SimpleDepartmentInfo> infos = enterpriseService.getDepartments(enterpriseId);
        return new Response<>(infos);
    }
}
