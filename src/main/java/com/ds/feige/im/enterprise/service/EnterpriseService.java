package com.ds.feige.im.enterprise.service;

import java.util.List;

import com.ds.feige.im.enterprise.dto.*;

public interface EnterpriseService {
    /**
     * 创建企业
     * 
     * @param name
     * @param description
     * @param createUserId
     */
    long createEnterprise(String name, String description, long createUserId);

    /**
     * 获取用户所属企业列表
     * 
     * @param userId
     */
    List<EnterpriseInfo> getEnterprises(long userId);

    /**
     * 创建部门
     *
     * @param request
     * @return 部门ID
     */
    long createDepartment(CreateDepRequest request);

    /**
     * 删除部门
     *
     * @param request
     * @return 部门基本信息
     */
    DepartmentBaseInfo deleteDepartment(DeleteDepRequest request);

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
    void editEmp(EditEmpRequest request);

    /**
     * 编辑员工部门信息
     *
     * @param request
     */
    void editEmpDepartments(EditDepEmpRequest request);

    /**
     * 添加员工,前提是员工必须具备账号,在t_user表有数据
     * 
     * @param request
     */
    long createEmp(AddEmpRequest request);

    /**
     * 删除员工
     */
    void deleteEmp(DeleteEmpRequest request);

    /**
     * 获取部门信息
     *
     * @param departmentId
     *            部门ID
     * @param queryChild
     *            是否查询子部门
     */
    DepartmentDetails getDepartment(long enterpriseId, long departmentId, boolean queryChild);

    /**
     * @param enterpriseId
     * @param departmentId
     * @param groupId
     */
    void updateDepartmentGroup(long enterpriseId, long departmentId, long groupId);

    List<DepartmentBaseInfo> getDepartments(long enterpriseId);

    /**
     * 根据用户ID查询员工信息
     *
     * @param userId
     * @param enterpriseId
     * @return 员工信息
     */
    EmployeeInfo getEmp(long enterpriseId, long userId);

    /**
     * 获取企业全体员工列表
     *
     * @param enterpriseId
     * @return 企业所有员工列表
     */
    List<EmployeeInfo> getEmpsByEnt(long enterpriseId);

    /**
     * 判断用户是否为管理员
     *
     * @param enterpriseId
     * @param userId
     */
    boolean isAdmin(long enterpriseId, long userId);

    /**
     * 检查是否具备管理员权限
     * 
     * @param enterpriseId
     * @param userId
     * @throws com.ds.base.nodepencies.exception.WarnMessageException
     *             如果没有管理员权限会抛出异常
     */
    void checkAdmin(long enterpriseId, long userId);
}
