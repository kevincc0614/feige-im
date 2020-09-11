package com.ds.feige.im.test;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.FeigeIMBootstrap;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.dto.CreateDepRequest;
import com.ds.feige.im.enterprise.dto.CreateEmpRequest;
import com.ds.feige.im.enterprise.dto.EditDepEmpRequest;
import com.ds.feige.im.enterprise.service.EnterpriseService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = FeigeIMBootstrap.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
public class BaseTest {
    public static List<Long> user_ids = new ArrayList<>();
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
//        initUsers();
//        initEnterprise();
//        initGroup();
    }

    public void initEnterprise() {
        operator_id = user_ids.get(0);
        enterprise_id = enterpriseService.createEnterprise("测试企业1", "企业描述1", operator_id);
        CreateDepRequest createDepRequest = new CreateDepRequest();
        createDepRequest.setOperatorId(operator_id);
        createDepRequest.setEnterpriseId(enterprise_id);
        createDepRequest.setDepartmentName("第一个部门");
        createDepRequest.setDepartmentEnName("DYGBM");
        createDepRequest.setParentId(0);
        dep_id = enterpriseService.createDepartment(createDepRequest);
        for (long userId : user_ids) {
            if (userId == operator_id) {
                continue;
            }
            UserInfo userInfo = userService.getUserById(userId);
            CreateEmpRequest request = new CreateEmpRequest();
            request.setOperatorId(operator_id);
            request.setName(userInfo.getNickName());
            request.setTitle("开发工程师");
            request.setUserId(userId);
            request.setEnterpriseId(enterprise_id);
            enterpriseService.createEmployee(request);
            EditDepEmpRequest editEmpRequest = new EditDepEmpRequest();
            editEmpRequest.setEnterpriseId(enterprise_id);
            editEmpRequest.setOperatorId(operator_id);
            editEmpRequest.setUserId(userId);
            editEmpRequest.setLeader(false);
            editEmpRequest.setDepartmentId(dep_id);
            enterpriseService.addDepartmentEmployee(editEmpRequest);
        }

    }

    public void initUsers() {
        for (int i = 0; i < 10; i++) {
            UserRegisterRequest registerRequest = new UserRegisterRequest();
            registerRequest.setMobile("9389220" + i);
            registerRequest.setPassword("12345" + i);
            registerRequest.setSource("enterprise");
            try {
                long userId = userService.register(registerRequest);
                user_ids.add(userId);
            } catch (WarnMessageException e) {
                if (e.getCode().equals(FeigeWarn.ACCOUNT_REGISTERD.code())) {
                    UserInfo userInfo = userService.getUserByMobile(registerRequest.getMobile());
                    user_ids.add(userInfo.getUserId());
                }
            }
        }
        LOGGER.info("Register users ok");
    }

    public GroupInfo initGroup() {
        List<Long> members = user_ids;
        String groupName = "测试群1";
        long createUserId = user_ids.get(0);
        GroupInfo groupInfo = groupUserService.createGroup(members, groupName, createUserId);
        group_id = groupInfo.getGroupId();
        return groupInfo;
    }

    @After
    public void clear() {
//        user_ids.forEach(uid -> {
//            userService.unregisterUser(uid, uid);
//        });
//        LOGGER.info("Unregister users ok");

    }
}
