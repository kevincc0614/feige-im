package com.ds.feige.im.enterprise.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.enterprise.entity.DepartmentEmployee;
import com.ds.feige.im.enterprise.po.EmpDepBindingPo;

/**
 * @author DC
 */
public interface DepartmentEmployeeMapper extends BaseMapper<DepartmentEmployee> {
    @Select("SELECT * FROM t_department_employee WHERE enterprise_id=#{enterpriseId} AND user_id=#{userId} AND department_id=#{departmentId}")
    DepartmentEmployee getByDepartmentIdAndUserId(long enterpriseId, long departmentId, long userId);

    @Delete("DELETE FROM t_department_employee where department_id=#{departmentId}")
    int deleteByDepartmentId(long departmentId);

    @Select("SELECT * FROM t_department_employee WHERE enterprise_id=#{enterpriseId} AND  user_id=#{userId}")
    List<DepartmentEmployee> findUserDepartments(long enterpriseId, long userId);

    @Select("SELECT tde.leader leader,tde.department_id department_id,tde.user_id user_id,tde.enterprise_id enterprise_id,tde.leader leader,td.name department_name FROM t_department_employee tde LEFT JOIN t_department td on tde.department_id = td.id WHERE tde.enterprise_id=#{enterpriseId} AND  tde.user_id=#{userId} ")
    List<EmpDepBindingPo> findEmpDepBindings(long enterpriseId, long userId);

    @Delete("DELETE FROM t_department_employee where enterprise_id=#{enterpriseId} AND user_id=#{userId}")
    int deleteByUserId(long enterpriseId, long userId);

    @Select({"<script> ", "SELECT * FROM t_department_employee WHERE id IN ",
        "<foreach item='item' index='index' collection='departmentIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach>", "</script>"})
    List<DepartmentEmployee> findByDepIds(Collection<Long> departmentIds);

    @Select("SELECT  * FROM t_department_employee WHERE enterprise_id=#{enterpriseId}")
    List<DepartmentEmployee> findByEntId(long enterpriseId);

}
