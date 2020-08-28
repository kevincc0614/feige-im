package com.ds.feige.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.pojo.entity.DepartmentEmployee;
import org.apache.ibatis.annotations.Select;

public interface DepartmentEmployeeMapper extends BaseMapper<DepartmentEmployee> {

    @Select("SELECT COUNT(*) FROM t_department_employee where user_id=#{userId} and department_id=#{departmentId}")
    int countByUserIdAndDepartmentId(long userId,long departmentId);
}
