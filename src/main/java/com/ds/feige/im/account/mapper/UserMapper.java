package com.ds.feige.im.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.account.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author caedmon
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select count(1) from t_user where id=#{id} and deleted=0")
    int getCountById(@Param("id") long userId);

    @Select("select count(1) from t_user where mobile=#{mobile} and deleted=0")
    int getByMobile(@Param("mobile") String mobile);

    @Select("SELECT * FROM t_user where mobile=#{mobile} and password=#{password} and deleted=0")
    User getOne(@Param("mobile") String mobile, @Param("password") String password);

    @Select("SELECT * FROM t_user where mobile=#{mobile} and deleted=0")
    User getOneByMobile(@Param("mobile") String mobile);

    @Select({"<script> ",
            "SELECT * FROM t_user WHERE deleted=0 and id IN",
            "<foreach item='item' index='index' collection='userIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<User> findUserByIds(@Param("userIds") List<Long> userIds);
}
