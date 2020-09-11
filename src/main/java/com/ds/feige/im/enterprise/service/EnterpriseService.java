package com.ds.feige.im.enterprise.service;

import com.ds.feige.im.enterprise.dto.*;

import java.util.List;

public interface EnterpriseService {

    long createEnterprise(String name, String description, long operatorId);

    List<EnterpriseInfo> getEnterprises(long userId);

    /**
     * 创建部门
     *
     * @param request
     */
    long createDepartment(CreateDepRequest request);

    /**
     * 删除部门
     *
     * @param request
     */
    void deleteDepartment(DeleteDepRequest request);

    /**
     * 编辑部门信息
     *
     * @param request
     */
    void editDepartment(EditDepRequest request);

    /**
     * 编辑员工信息
     *
     * @param request
     */
    void editEmployee(EditEmpRequest request);

    /**
     * 把员工添加到部门
     *
     * @param request
     */
    void addDepartmentEmployee(EditDepEmpRequest request);

    /**
     * 删除部门员工
     *
     * @param request
     */
    void removeDepartmentEmployee(EditDepEmpRequest request);

    /**
     * 添加员工,前提是员工必须具备账号,在t_user表有数据
     */
    long createEmployee(CreateEmpRequest request);

    /**
     * 删除员工
     */
    void deleteEmployee(long enterpriseId, long userId);

    /**
     * 获取部门信息
     *
     * @param departmentId 部门ID
     * @param queryChild   是否查询子部门
     */
    DepartmentInfo getDepartment(long enterpriseId, long departmentId, boolean queryChild);

    List<SimpleDepartmentInfo> getDepartments(long enterpriseId);

    /**
     * 根据用户ID查询员工信息
     *
     * @param userId
     * @param enterpriseId
     * @return 员工信息
     */
    EmployeeInfo getEmployeeByUserId(long enterpriseId, long userId);

    /**
     * 获取企业全体员工列表
     *
     * @param enterpriseId
     */
    List<EmployeeInfo> getEmployees(long enterpriseId);

    /**
     * 判断用户是否为管理员
     *
     * @param enterpriseId
     * @param userId
     */
    boolean isAdmin(long enterpriseId, long userId);
}
