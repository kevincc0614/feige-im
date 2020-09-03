package com.ds.feige.im.enterprise.controller;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.enterprise.dto.DepartmentInfo;
import com.ds.feige.im.enterprise.dto.EmployeeInfo;
import com.ds.feige.im.enterprise.dto.GetDepRequest;
import com.ds.feige.im.enterprise.dto.GetEmpRequest;
import com.ds.feige.im.enterprise.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author DC
 */
@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {
    @Autowired
    EnterpriseService enterpriseService;

    @RequestMapping(value = "/department/info")
    Response<DepartmentInfo> getDepartment(@RequestBody GetDepRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long departmentId = request.getDepartmentId();
        boolean isQueryChild = request.isQueryChild();
        DepartmentInfo departmentInfo = enterpriseService.getDepartment(enterpriseId, departmentId, isQueryChild);
        return new Response<>(departmentInfo);
    }

    @RequestMapping("/employee/info")
    Response<EmployeeInfo> getEmployeeInfo(@RequestBody GetEmpRequest request) {
        EmployeeInfo employeeInfo = enterpriseService.getEmployeeByUserId(request.getEnterpriseId(), request.getUserId());
        return new Response<>(employeeInfo);
    }

}
