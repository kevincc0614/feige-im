package com.ds.feige.im.enterprise.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.enterprise.entity.EmpRoleBinding;

/**
 * @author DC
 */
public interface EmpRoleBindingMapper extends BaseMapper<EmpRoleBinding> {

    @Delete("DELETE FROM t_emp_role_binding WHERE enterprise_id=#{enterpriseId} AND user_id=#{userId} ")
    int deleteByEmp(long enterpriseId, long userId);

    @Select("SELECT * FROM t_emp_role_binding WHERE enterprise_id=#{enterpriseId} AND user_id=#{userId}")
    List<EmpRoleBinding> findByEmp(long enterpriseId, long userId);
}
