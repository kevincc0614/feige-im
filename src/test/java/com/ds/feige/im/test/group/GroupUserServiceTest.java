package com.ds.feige.im.test.group;

import com.ds.feige.im.pojo.dto.group.GroupInfo;
import com.ds.feige.im.service.group.GroupUserService;
import com.ds.feige.im.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * 群聊测试类
 *
 * @author DC
 */
public class GroupUserServiceTest extends BaseTest {
    @Autowired
    GroupUserService groupUserService;
    static final long USER_ID_1=2008141150478262272L;
    static final long USER_ID_2=2008141140050280448L;
    @Test
    public void testCreateGroup(){
        List<Long> userIds= Arrays.asList(USER_ID_1,USER_ID_2);
        GroupInfo result =groupUserService.createGroup(userIds,null,USER_ID_1);
        System.out.println(result);
    }
}
