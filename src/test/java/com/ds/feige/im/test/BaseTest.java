package com.ds.feige.im.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.feige.im.IMBootstrap;
import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.enterprise.service.EnterpriseService;

@SpringBootTest(classes = IMBootstrap.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
public class BaseTest {
    public static Set<Long> user_ids = new HashSet<>();
    public static long group_id;
    public static long enterprise_id;
    public static long dep_id;
    public static long operator_id;
    static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
    @Autowired
    protected
    UserService userService;
    @Autowired
    protected GroupUserService groupUserService;
    @Autowired
    protected EnterpriseService enterpriseService;

    @Before
    public void init() {
    }

    public void initEnterprise() {

    }

    public void initUsers() {
        for (int i = 0; i < 10; i++) {
            UserRegisterRequest registerRequest = new UserRegisterRequest();
            registerRequest.setLoginName("9389220" + i);
            registerRequest.setPassword("12345" + i);
            registerRequest.setSource("enterprise");
        }
        LOGGER.info("Register users ok");
    }

    public GroupInfo initGroup() {
        Set<Long> members = user_ids;
        String groupName = "测试群1";
        long createUserId = (Long)user_ids.toArray()[0];
        GroupInfo groupInfo = groupUserService.createGroup(members, groupName, createUserId);
        group_id = groupInfo.getGroupId();
        return groupInfo;
    }

    @After
    public void clear() {

    }
}
