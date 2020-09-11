package com.ds.feige.im.enterprise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.enterprise.dto.DepartmentInfo;
import com.ds.feige.im.enterprise.entity.Department;
import org.apache.ibatis.annotations.Select;

import java.util.List;
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT id,parent_id,name,priority FROM t_department where parent_id=#{parentId}")
    List<DepartmentInfo> findByParentId(long enterpriseId, long parentId);

    @Select("SELECT COUNT(*) FROM t_department where enterprise_id=#{enterpriseId} AND parent_id=#{parentId} AND name=#{name}")
    int countSameNameDepartment(long enterpriseId, long parentId, String name);

    @Select("SELECT * FROM t_department where enterprise_id=#{enterpriseId}")
    List<Department> findByEnterpriseId(long enterpriseId);
}
