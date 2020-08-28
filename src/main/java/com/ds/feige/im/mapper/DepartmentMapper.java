package com.ds.feige.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.pojo.dto.enterprise.DepartmentInfo;
import com.ds.feige.im.pojo.entity.Department;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT id,parent_id,name,priority FROM t_department where parent_id=#{parentId}")
    List<DepartmentInfo> findByParentId(long parentId);

    @Select("SELECT COUNT(*) FROM t_department where parent_id=#{parentId} and name=#{name}")
    int countByParentIdAndName(long parentId, String name);
}
