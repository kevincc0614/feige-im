package com.ds.feige.im.enterprise.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.enterprise.dto.DepartmentDetails;
import com.ds.feige.im.enterprise.entity.Department;

public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT id,parent_id,name,priority FROM t_department where parent_id=#{parentId}")
    List<DepartmentDetails> findByParentId(long enterpriseId, long parentId);

    @Select("SELECT COUNT(*) FROM t_department where enterprise_id=#{enterpriseId} AND parent_id=#{parentId} AND name=#{name}")
    int countSameNameDepartment(long enterpriseId, long parentId, String name);

    @Select("SELECT * FROM t_department where enterprise_id=#{enterpriseId}")
    List<Department> findByEnterpriseId(long enterpriseId);

    @Update("UPDATE t_department SET group_id=#{groupId} WHERE id=#{departmentId} AND create_group=1")
    int updateGroupId(long enterpriseId, long departmentId, long groupId);
}
