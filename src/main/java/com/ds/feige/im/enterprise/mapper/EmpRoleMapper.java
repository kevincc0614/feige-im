package com.ds.feige.im.enterprise.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.enterprise.entity.EmpRole;

/**
 * @author DC
 */
public interface EmpRoleMapper extends BaseMapper<EmpRole> {
    @Delete("DELETE FROM t_emp_role WHERE enterprise_id=#{enterpriseId} and id=#{roleId}")
    int deleteByEntIdAndId(long roleId, long enterpriseId);

    @Select({"<script> ", "SELECT id FROM t_emp_role WHERE enterprise_id=#{enterpriseId} AND  id IN ",
        "<foreach item='item' index='index' collection='roleIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach>", "</script>"})
    List<Long> findExistsRoleIds(long enterpriseId, Collection<Long> roleIds);

    @Select("SELECT * FROM t_emp_role where enterprise_id=#{enterpriseId}")
    List<EmpRole> findByEnterpriseId(long enterpriseId);

}
