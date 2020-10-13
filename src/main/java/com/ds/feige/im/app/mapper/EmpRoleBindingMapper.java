package com.ds.feige.im.app.mapper;

import org.apache.ibatis.annotations.Delete;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.app.entity.EmpRoleBinding;

/**
 * @author DC
 */
public interface EmpRoleBindingMapper extends BaseMapper<EmpRoleBinding> {

    @Delete("DELETE FROM t_app_emp_role_binding WHERE enterprise_id=#{enterpriseId} AND user_id=#{userId} ")
    int deleteByEntAndUser(long enterpriseId, long userId);
}
