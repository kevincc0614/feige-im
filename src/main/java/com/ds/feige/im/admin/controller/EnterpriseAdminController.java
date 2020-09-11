package com.ds.feige.im.admin.controller;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 企业管理
 *
 * @author DC
 */
@RequestMapping("/admin/enterprise")
public class EnterpriseAdminController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    UserService userService;

    @RequestMapping("/department/info")
    public Response<DepartmentInfo> departmentInfo(@RequestBody GetDepRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long departmentId = request.getDepartmentId();
        boolean isQueryChild = request.isQueryChild();
        DepartmentInfo departmentInfo = enterpriseService.getDepartment(enterpriseId, departmentId, isQueryChild);
        return new Response<>(departmentInfo);
    }

    @RequestMapping("/department/delete")
    public Response deleteDepartment(HttpServletRequest request, @RequestBody DeleteDepRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.deleteDepartment(body);
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/employee/list")
    public Response<List<EmployeeInfo>> employeeList(long enterpriseId) {
        List<EmployeeInfo> employeeInfoList = enterpriseService.getEmployees(enterpriseId);
        return new Response(employeeInfoList);
    }

    @RequestMapping("/employee/create")
    public Response<List<EmployeeInfo>> createEmployee(HttpServletRequest request, CreateEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.createEmployee(body);
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/department/edit")
    public Response editDepartment(HttpServletRequest request, @RequestBody EditDepRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.editDepartment(body);
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/employee/edit")
    public Response editEmployee(HttpServletRequest request, @RequestBody EditEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.editEmployee(body);
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/department/add-employee")
    public Response addDepartmentEmployee(HttpServletRequest request, @RequestBody EditDepEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.addDepartmentEmployee(body);
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/department/remove-employee")
    public Response removeDepartmentEmployee(HttpServletRequest request, @RequestBody EditDepEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.removeDepartmentEmployee(body);
        return Response.EMPTY_SUCCESS;
    }
}
