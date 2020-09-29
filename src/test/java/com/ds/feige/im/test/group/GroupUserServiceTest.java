package com.ds.feige.im.test.group;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.test.BaseTest;

/**
 * 群聊测试类
 *
 * @author DC
 */
public class GroupUserServiceTest extends BaseTest {
    @Test
    public void testJoinGroup() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setSource("enterprise");
        registerRequest.setPassword("123456");
        String mobile = RandomStringUtils.randomNumeric(10);
        registerRequest.setMobile(mobile);
        long userId = userService.register(registerRequest);
        // groupUserService.inviteJoinGroup(group_id, Sets.newHashSet(userId), user_ids.get(0));
    }

}
