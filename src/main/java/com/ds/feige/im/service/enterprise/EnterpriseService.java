package com.ds.feige.im.service.enterprise;

import com.ds.feige.im.pojo.dto.enterprise.CreateDepartmentRequest;
import com.ds.feige.im.pojo.dto.enterprise.CreateEmployeeRequest;
import com.ds.feige.im.pojo.dto.enterprise.DepartmentInfo;
import com.ds.feige.im.pojo.dto.enterprise.EmployeeInfo;

public interface EnterpriseService {

    /**
     * 创建部门
     *
     * @param request
     */
    long createDepartment(CreateDepartmentRequest request);

    /**
     * 把员工添加到部门
     */
    void addEmployeeToDepartment(long userId, long departmentId, boolean leader, long operatorId);

    /**
     * 添加员工
     */
    long createEmployee(CreateEmployeeRequest request);

    /**
     * 获取部门信息
     *
     * @param departmentId 部门ID
     * @param queryChild   是否查询子部门
     */
    DepartmentInfo getDepartment(long departmentId, boolean queryChild);

    /**
     * 根据用户ID查询员工信息
     *
     * @param userId
     * @return 员工信息
     */
    EmployeeInfo getEmployeeByUserId(long userId);
}
