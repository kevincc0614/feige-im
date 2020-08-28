package com.ds.feige.im.controller.http;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.pojo.dto.enterprise.DepartmentInfo;
import com.ds.feige.im.pojo.dto.enterprise.EmployeeInfo;
import com.ds.feige.im.service.enterprise.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/get-department")
    Response<DepartmentInfo> getDepartment( long departmentId, boolean queryChild) {
        DepartmentInfo departmentInfo = enterpriseService.getDepartment(departmentId, queryChild);
        return new Response(departmentInfo);
    }

    @RequestMapping("/get-employee")
    Response<EmployeeInfo> getEmployeeInfo(long userId) {
        EmployeeInfo employeeInfo = enterpriseService.getEmployeeByUserId(userId);
        return new Response<>(employeeInfo);
    }
}