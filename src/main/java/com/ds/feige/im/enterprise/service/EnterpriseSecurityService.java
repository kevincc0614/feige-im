package com.ds.feige.im.enterprise.service;

import java.util.List;

import com.ds.feige.im.enterprise.dto.*;

/**
 * @author DC
 */
public interface EnterpriseSecurityService {

    boolean checkPermission(PermissionCheckRequest request);

    /**
     * 编辑角色和权限的绑定,先清空后新增
     */
    void editEmpRoleBindings(EditEmpRoleBindingRequest request);

    void deleteEmpAllRoles(long enterpriseId, long userId);

    /**
     * 角色信息创建或修改
     * 
     * @param request
     */
    long editEmpRole(EditEmpRoleRequest request);

    /**
     * 删除角色
     */
    boolean deleteEmpRole(DeleteEmpRoleRequest request);

    /**
     * 获取企业下所有角色信息
     * 
     * @param request
     */
    List<EmpRoleInfo> getEnterpriseRoles(EnterpriseOpRequest request);

    /**
     * 创建权限
     * 
     * @param createRoleAuthority
     */
    long createRoleAuthority(CreateRoleAuthority createRoleAuthority);

    /**
     * 删除权限
     * 
     * @param authorityId
     */
    void deleteRoleAuthority(long authorityId);

    List<RoleAuthorityInfo> getRoleAuthorities(RoleAuthoritiesQuery query);

    List<RoleAuthorityInfo> getEmpAuthorities(long enterpriseId, long userId);
}
