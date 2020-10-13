package com.ds.feige.im.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.app.dto.RoleAuthorityInfo;
import com.ds.feige.im.app.entity.RoleAuthority;

/**
 * @author DC
 */
public interface RoleAuthorityMapper extends BaseMapper<RoleAuthority> {
    @Select("SELECT ard.app_id app_id,aer.role_id role_id,ard.resource resource,ard.method method,ard.update_time update_time,ard.create_time create_time FROM t_app_emp_role_binding aer LEFT JOIN t_app_role_authority ard ON aer.role_id=ard.role_id "
        + "WHERE ard.app_id=#{appId} AND aer.user_id=#{userId} and ard.resource=#{resource} and ard.method=#{method} limit 1")
    RoleAuthority getByOperation(long appId, long userId, String resource, String method);

    @Select("SELECT app_id,role_id,resource,method,update_time,create_time FROM t_app_role_authority WHERE role_id=#{roleId} AND resource=#{resource} AND method=#{method}")
    RoleAuthority getByRoleAndResourceAndMethod(long roleId, String resource, String method);

    @Select("SELECT app_id,role_id,resource,method,update_time,create_time FROM t_app_role_authority WHERE role_id=#{roleId} AND name=#{name}")
    RoleAuthority getByRoleIdAndName(long roleId, String name);

    @Select("SELECT tara.id authority_id,tara.app_id app_id,tara.role_id,tara.name authority_name,tara.resource,tara.method,ta.name app_name,taer.role_name role_name "
        + " FROM t_app_role_authority tara INNER JOIN t_app_emp_role taer ON tara.role_id=taer.id INNER JOIN t_app ta ON ta.id=tara.app_id WHERE role_id=#{roleId}")
    List<RoleAuthorityInfo> findByRoleId(long roleId);
}
