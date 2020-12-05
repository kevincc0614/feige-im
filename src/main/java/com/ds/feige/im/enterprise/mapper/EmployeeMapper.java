package com.ds.feige.im.enterprise.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.enterprise.dto.EmpDetails;
import com.ds.feige.im.enterprise.entity.Employee;

public interface EmployeeMapper extends BaseMapper<Employee> {
    @Select("SELECT e.user_id user_id,e.name name,e.avatar avatar,e.title title,e.work_email work_email,de.leader leader " +
            "FROM t_employee e LEFT JOIN t_department_employee de ON e.user_id=de.user_id where de.department_id=#{departmentId} order by de.leader DESC")
    List<EmpDetails> findByDepartmentId(long enterpriseId, long departmentId);

    @Select("SELECT * FROM t_employee WHERE user_id=#{userId} and enterprise_id=#{enterpriseId}")
    Employee getByUserId(long enterpriseId, long userId);

    @Select("SELECT * FROM t_employee WHERE enterprise_id=#{enterpriseId}")
    List<Employee> findByEnterpriseId(long enterpriseId);

    @Delete("DELETE FROM t_employee WHERE enterprise_id=#{enterpriseId} and user_id=#{userId}")
    int deleteEmployee(long enterpriseId, long userId);

}
