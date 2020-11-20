package com.ds.feige.im.enterprise.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.app.service.AppService;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.entity.EmpRole;
import com.ds.feige.im.enterprise.entity.EmpRoleBinding;
import com.ds.feige.im.enterprise.entity.RoleAuthority;
import com.ds.feige.im.enterprise.mapper.EmpRoleBindingMapper;
import com.ds.feige.im.enterprise.mapper.EmpRoleMapper;
import com.ds.feige.im.enterprise.mapper.RoleAuthorityMapper;
import com.google.common.collect.Lists;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Service
@Slf4j
public class EnterpriseSecurityServiceImpl extends ServiceImpl<EmpRoleBindingMapper, EmpRoleBinding>
    implements EnterpriseSecurityService {
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
        List<RoleAuthorityInfo> authorityInfos = getEmpAuthorities(operation.getEnterpriseId(), operation.getUserId());
        for (RoleAuthorityInfo info : authorityInfos) {
            if (info.getAppId().equals(operation.getAppId()) && info.getResource().equals(operation.getResource())
                && info.getMethod().equals(operation.getMethod())) {
                return true;
            }
        }
        log.warn("No operation authority:{}", operation);
        return false;
    }

    @Override
    public void deleteEmpAllRoles(long enterpriseId, long userId) {
        empRoleBindingMapper.deleteByEmp(enterpriseId, userId);
    }

    @Override
    public void editEmpRoleBindings(EditEmpRoleBindingRequest request) {
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            log.warn("Edit role bindings fail,roles is empty:userId={}", request.getUserId());
            return;
        }
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
        empRoleBindingMapper.deleteByEmp(request.getEnterpriseId(), request.getUserId());
        boolean saved = super.saveBatch(bindings);
        log.info("Batch save EmpRoleBindings:success={},bindings={}", saved, bindings);
    }

    @SneakyThrows
    @Override
    public long editEmpRole(EditEmpRoleRequest request) {
        log.info("Edit empRole start:request={}", request);
        EmpRole empRole = null;
        if (request.getRoleId() != null) {
            empRole = empRoleMapper.selectById(request.getRoleId());
        }
        boolean insert = false;
        if (empRole == null) {
            empRole = new EmpRole();
            insert = true;
        }
        empRole.setRoleName(request.getRoleName());
        empRole.setEnterpriseId(request.getEnterpriseId());
        // 判断权限是否在数据库中存在
        List<RoleAuthorityInfo> authorities =
            roleAuthorityMapper.findByIds(request.getEnterpriseId(), request.getAuthorityIds());
        if (authorities == null || authorities.isEmpty()) {
            throw new WarnMessageException(FeigeWarn.AUTHORITY_IS_EMPTY);
        }
        List<Long> ids = Lists.newArrayListWithCapacity(authorities.size());
        for (RoleAuthorityInfo authority : authorities) {
            ids.add(authority.getAuthorityId());
        }
        empRole.setAuthorityIds(StringUtils.collectionToDelimitedString(ids, ","));
        if (insert) {
            empRoleMapper.insert(empRole);
        } else {
            empRoleMapper.updateById(empRole);
        }
        return empRole.getId();
    }

    @Override
    public boolean deleteEmpRole(DeleteEmpRoleRequest request) {
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
        // TODO 判断是否存在重复的权限
        long appId = createRoleAuthority.getAppId();
        RoleAuthority authority = new RoleAuthority();
        authority.setEnterpriseId(createRoleAuthority.getEnterpriseId());
        authority.setAppId(appId);
        authority.setResource(createRoleAuthority.getResource());
        authority.setMethod(createRoleAuthority.getMethod());
        authority.setName(createRoleAuthority.getName());
        roleAuthorityMapper.insert(authority);
        return authority.getId();
    }

    @Override
    public void deleteRoleAuthority(long authorityId) {
        roleAuthorityMapper.deleteById(authorityId);
    }

    @SneakyThrows
    @Override
    public List<RoleAuthorityInfo> getRoleAuthorities(RoleAuthoritiesQuery query) {
        List<RoleAuthorityInfo> result = null;
        Long roleId = query.getRoleId();
        if (roleId != null) {
            EmpRole role = empRoleMapper.selectById(roleId);
            if (role == null) {
                throw new WarnMessageException(FeigeWarn.ROLE_NOT_EXISTS);
            }
            String authorityIds = role.getAuthorityIds();
            Set<String> list = StringUtils.commaDelimitedListToSet(authorityIds);
            Set<Long> idSet = new HashSet<>();
            list.forEach(s -> idSet.add(Long.valueOf(s)));
            result = roleAuthorityMapper.findByIds(query.getEnterpriseId(), idSet);
        } else {
            result = roleAuthorityMapper.findByEnt(query.getEnterpriseId());
        }
        return result;
    }

    @Override
    public List<RoleAuthorityInfo> getEmpAuthorities(long enterpriseId, long userId) {
        List<EmpRoleBinding> bindings = empRoleBindingMapper.findByEmp(enterpriseId, userId);
        List<RoleAuthorityInfo> result = Lists.newArrayList();
        bindings.forEach(binding -> {
            long roleId = binding.getRoleId();
            RoleAuthoritiesQuery query = new RoleAuthoritiesQuery();
            query.setEnterpriseId(enterpriseId);
            query.setRoleId(roleId);
            List<RoleAuthorityInfo> authorityInfos = getRoleAuthorities(query);
            result.addAll(authorityInfos);
        });
        return result;
    }

}
