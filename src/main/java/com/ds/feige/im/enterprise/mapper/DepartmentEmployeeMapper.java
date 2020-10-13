package com.ds.feige.im.enterprise.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.enterprise.entity.DepartmentEmployee;

/**
 * @author DC
 */
public interface DepartmentEmployeeMapper extends BaseMapper<DepartmentEmployee> {

    @Select("SELECT COUNT(*) FROM t_department_employee where user_id=#{userId} and department_id=#{departmentId}")
    DepartmentEmployee getByUserAndDepartmentId(long userId, long departmentId);

    @Delete("DELETE FROM t_department_employee where department_id=#{departmentId}")
    int deleteByDepartmentId(long departmentId);

    @Delete("DELETE FROM t_department_employee where enterprise_id=#{enterpriseId} AND department_id=#{departmentId} AND user_id=#{userId}")
    int deleteDepartmentEmployee(long enterprise, long departmentId, long userId);

    @Select("SELECT department_id FROM t_department_employee WHERE user_id=#{userId}")
    List<Long> findDepartments(long userId);

    @Delete("DELETE FROM t_department_employee where enterprise_id=#{enterpriseId} AND user_id=#{userId}")
    int deleteByUserId(long enterpriseId, long userId);
}
