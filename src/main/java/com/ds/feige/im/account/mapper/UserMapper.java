package com.ds.feige.im.account.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.account.entity.User;

/**
 * @author caedmon
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select count(1) from t_user where id=#{id}")
    int getCountById(@Param("id") long userId);

    @Select("select count(1) from t_user where mobile=#{mobile} ")
    int getByMobile(@Param("mobile") String mobile);

    @Select("SELECT * FROM t_user where mobile=#{mobile} and password=#{password} ")
    User getOne(@Param("mobile") String mobile, @Param("password") String password);

    @Select("SELECT * FROM t_user where mobile=#{mobile}")
    User getOneByMobile(@Param("mobile") String mobile);

    @Select({"<script> ", "SELECT * FROM t_user WHERE id IN ",
        "<foreach item='item' index='index' collection='userIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach>", "</script>"})
    List<User> findUserByIds(@Param("userIds") Collection<Long> userIds);
}
