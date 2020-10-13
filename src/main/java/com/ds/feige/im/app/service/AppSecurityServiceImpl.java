package com.ds.feige.im.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.app.dto.*;
import com.ds.feige.im.app.entity.EmpRole;
import com.ds.feige.im.app.entity.EmpRoleBinding;
import com.ds.feige.im.app.entity.RoleAuthority;
import com.ds.feige.im.app.mapper.EmpRoleBindingMapper;
import com.ds.feige.im.app.mapper.EmpRoleMapper;
import com.ds.feige.im.app.mapper.RoleAuthorityMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.dto.EnterpriseOpRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Service
@Transactional
@Slf4j
public class AppSecurityServiceImpl extends ServiceImpl<EmpRoleBindingMapper, EmpRoleBinding>
    implements AppSecurityService {
    @Autowired
    AppService appService;
    @Autowired
    EmpRoleBindingMapper empRoleBindingMapper;
    @Autowired
    RoleAuthorityMapper roleAuthorityMapper;
    @Autowired
    EmpRoleMapper empRoleMapper;

    @Override
    public boolean checkPermission(PermissionCheckRequest operation) {
        RoleAuthority authority = roleAuthorityMapper.getByOperation(operation.getAppId(), operation.getUserId(),
            operation.getResource(), operation.getMethod());
        if (authority == null) {
            log.warn("No operation authority:{}", operation);
            return false;
        }
        return true;
    }

    @Override
    public void editEmpRoleBindings(EditEmpRoleBindingRequest request) {
        // 判断roles是否存在
        List<Long> existsRoleIds = empRoleMapper.findExistsRoleIds(request.getEnterpriseId(), request.getRoles());
        List<EmpRoleBinding> bindings = new ArrayList<>();
        existsRoleIds.forEach(roleId -> {
            EmpRoleBinding er = new EmpRoleBinding();
            er.setEnterpriseId(request.getEnterpriseId());
            er.setRoleId(roleId);
            er.setUserId(request.getUserId());
            bindings.add(er);
        });
        empRoleBindingMapper.deleteByEntAndUser(request.getEnterpriseId(), request.getUserId());
        boolean saved = super.saveBatch(bindings);
        log.info("Batch save EmpRoleBindings:success={},bindings={}", saved, bindings);
    }

    @Override
    public long createEmpRole(CreateEmpRoleRequest request) {
        EmpRole empRole = new EmpRole();
        empRole.setRoleName(request.getRoleName());
        empRole.setEnterpriseId(request.getEnterpriseId());
        empRoleMapper.insert(empRole);
        return empRole.getId();
    }

    @Override
    public boolean deleteEmpRole(DeleteEmpRoleRequest request) {
        // TODO 是否要先删除角色相关的绑定关系数据
        int i = empRoleMapper.deleteByEntIdAndId(request.getRoleId(), request.getEnterpriseId());
        return i == 1;

    }

    @Override
    public List<EmpRoleInfo> getEnterpriseRoles(EnterpriseOpRequest request) {
        List<EmpRole> empRoles = empRoleMapper.findByEnterpriseId(request.getEnterpriseId());
        return BeansConverter.empRoleTosEmpRoleInfos(empRoles);
    }

    @Override
    public long createRoleAuthority(CreateRoleAuthority createRoleAuthority) {
        // 判断角色ID是否存在
        EmpRole role = empRoleMapper.selectById(createRoleAuthority.getRoleId());
        if (role == null) {
            throw new WarnMessageException(FeigeWarn.APP_ROLE_NOT_EXISTS);
        }
        long roleId = role.getId();
        // 判断是否存在重复的权限
        RoleAuthority byResourceAndMethod = roleAuthorityMapper.getByRoleAndResourceAndMethod(
            createRoleAuthority.getRoleId(), createRoleAuthority.getResource(), createRoleAuthority.getMethod());
        if (byResourceAndMethod != null) {
            throw new WarnMessageException(FeigeWarn.APP_ROLE_AUTHORITY_EXISTS);
        }
        RoleAuthority byName = roleAuthorityMapper.getByRoleIdAndName(roleId, createRoleAuthority.getName());
        if (byName != null) {
            throw new WarnMessageException(FeigeWarn.APP_ROLE_AUTHORITY_NAME_EXISTS);
        }
        long appId = createRoleAuthority.getAppId();
        RoleAuthority authority = new RoleAuthority();
        authority.setAppId(appId);
        authority.setResource(createRoleAuthority.getResource());
        authority.setMethod(createRoleAuthority.getMethod());
        authority.setRoleId(roleId);
        authority.setName(createRoleAuthority.getName());
        roleAuthorityMapper.insert(authority);
        return authority.getId();
    }

    @Override
    public void deleteRoleAuthority(long authorityId) {
        roleAuthorityMapper.deleteById(authorityId);
    }

    @Override
    public List<RoleAuthorityInfo> getRoleAuthorities(long roleId) {
        List<RoleAuthorityInfo> list = roleAuthorityMapper.findByRoleId(roleId);
        return list;
    }
}
