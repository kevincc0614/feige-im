package com.ds.feige.im.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.entity.GroupUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 群和用户关系表
 *
 * @author DC
 */
public interface GroupUserMapper extends BaseMapper<GroupUser> {

    @Select("SELECT gu.user_id FROM t_group_user gu  WHERE gu.group_id=#{groupId}")
    List<Long> findUserIdsByGroup(long groupId);

    @Delete("DELETE FROM t_group_user where group_id=#{groupId}")
    int disbandGroup(long groupId);

    @Select("SELECT * FROM t_group_user WHERE group_id=#{groupId} and user_id=#{userId}")
    GroupUser getGroupUser(long groupId, long userId);

    @Update("UPDATE t_group_user SET role=#{role} WHERE group_id=#{groupId} and user_id=#{userId}")
    int updateUserRole(long groupId, long userId, String role);

    @Delete("DELETE FROM t_group_user where group_id=#{groupId} and user_id=#{userId}")
    int deleteByGroupAndUserId(long groupId, long userId);
}
