package com.ds.feige.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.pojo.dto.enterprise.EmployeeInfo;
import com.ds.feige.im.pojo.entity.Employee;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EmployeeMapper extends BaseMapper<Employee> {
    @Select("SELECT e.user_id user_id,e.name name,e.title title,e.work_email work_email,de.leader leader " +
            "FROM t_employee e LEFT JOIN t_department_employee de ON e.user_id=de.user_id where de.department_id=#{departmentId} order by de.leader DESC")
    List<EmployeeInfo> findByDepartmentId(long departmentId);

    @Select("SELECT * FROM t_employee WHERE user_id=#{userId}")
    Employee getByUserId(long userId);
}
