package com.ds.feige.im.enterprise.service;

import com.ds.feige.im.enterprise.dto.*;

import java.util.List;

public interface EnterpriseService {


    List<EnterpriseInfo> getEnterprises(long userId);

    /**
     * 创建部门
     *
     * @param request
     */
    long createDepartment(CreateDepRequest request);

    /**
     * 删除部门
     */
    void deleteDepartment(DeleteDepRequest request);

    /**
     * 编辑部门信息
     */
    void editDepartment(EditDepRequest request);

    /**
     * 编辑员工信息
     */
    void editEmployee(EditEmpRequest request);

    /**
     * 把员工添加到部门
     */
    void addDepartmentEmployee(EditDepEmpRequest request);

    /**
     * 删除部门员工
     */
    void removeDepartmentEmployee(EditDepEmpRequest request);

    /**
     * 添加员工
     */
    long createEmployee(CreateEmpRequest request);

    /**
     * 获取部门信息
     *
     * @param departmentId 部门ID
     * @param queryChild   是否查询子部门
     */
    DepartmentInfo getDepartment(long enterpriseId, long departmentId, boolean queryChild);

    /**
     * 根据用户ID查询员工信息
     *
     * @param userId
     * @param enterpriseId
     * @return 员工信息
     */
    EmployeeInfo getEmployeeByUserId(long enterpriseId, long userId);

    /**
     * 获取员工列表
     */
    List<EmployeeInfo> getEmployees(long enterpriseId);

    boolean isAdmin(long enterpriseId, long userId);
}
