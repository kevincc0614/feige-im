package com.ds.feige.im.test.user;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.GetTokenRequest;
import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceTest extends BaseTest {
    static final Logger LOGGER= LoggerFactory.getLogger(UserServiceTest.class);

    @Test
    public void testRegister() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setMobile("110110110");
        userRegisterRequest.setPassword("123456");
        userRegisterRequest.setSource("enterprise");
        long userId = userService.register(userRegisterRequest);
        Assert.assertNotNull("注册失败", userId);
        LOGGER.info("Register ok:userId={}", userId);
        try {
            userService.register(userRegisterRequest);
        } catch (WarnMessageException e) {
            org.junit.Assert.assertEquals("重复注册非检测出", FeigeWarn.ACCOUNT_REGISTERD.code(), e.getCode());
        }
        Assert.assertEquals("注销失败", true, userService.deleteUser(userId));
        ;
    }

    @Test
    public void testUnregister() {
        userService.unregisterUser(2009032107297914880L, 1L);
    }

    @Test
    public void testGetToken() {
        GetTokenRequest request = new GetTokenRequest();
        request.setLoginName("110110110");
        request.setPassword("1234567");
        String token = userService.getToken(request);
        System.out.println("Token:" + token);
        userService.verifyToken(token);
    }
}
