package com.ds.feige.im.enterprise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.enterprise.entity.Enterprise;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author DC
 */
public interface EnterpriseMapper extends BaseMapper<Enterprise> {
    @Select("SELECT a.id,a.name,a.description FROM t_enterprise a RIGHT JOIN t_employee b ON  a.id=b.enterprise_id WHERE user_id=#{userId} GROUP BY a.id")
    List<Enterprise> findByUserId(long userId);
}
