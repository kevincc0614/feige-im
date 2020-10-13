package com.ds.feige.im.app.service;

import java.util.List;

import com.ds.feige.im.app.dto.*;
import com.ds.feige.im.enterprise.dto.EnterpriseOpRequest;

/**
 * @author DC App用户安全权限控制
 */
public interface AppSecurityService {

    boolean checkPermission(PermissionCheckRequest request);

    /**
     * 编辑用户和权限的绑定,先清空后新增
     */
    void editEmpRoleBindings(EditEmpRoleBindingRequest request);

    long createEmpRole(CreateEmpRoleRequest request);

    boolean deleteEmpRole(DeleteEmpRoleRequest request);

    List<EmpRoleInfo> getEnterpriseRoles(EnterpriseOpRequest request);

    long createRoleAuthority(CreateRoleAuthority createRoleAuthority);

    void deleteRoleAuthority(long authorityId);

    List<RoleAuthorityInfo> getRoleAuthorities(long roleId);
}
