package com.ds.feige.im.test.enterprise;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.service.EnterpriseService;
import com.ds.feige.im.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EnterpriseServiceTest extends BaseTest {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    UserService userService;
    static final long USER_ID=2008141150478262272L;
    static final long DEP_ID = 2008171236913319936L;
    static final long ENT_ID = 1L;
    @Test
    public void testCreateEmp() {
        UserInfo userInfo = userService.getUserById(USER_ID);
        CreateEmpRequest request = new CreateEmpRequest();
        request.setUserId(userInfo.getUserId());
        request.setName(userInfo.getNickName());
        request.setTitle("CEO");
        request.setWorkEmail(userInfo.getEmail());
        long employeeId = enterpriseService.createEmployee(request);
        Assert.assertEquals(true, employeeId > 0);
        EmployeeInfo employeeInfo = enterpriseService.getEmployeeByUserId(ENT_ID, userInfo.getUserId());
        Assert.assertEquals(userInfo.getUserId(), employeeInfo.getUserId());
        System.out.println("员工用户ID:" + employeeInfo.getUserId());
    }
    @Test
    public void testCreateDepartment() {
        CreateDepRequest request = new CreateDepRequest();
        final String departmentName = "总裁办";
        request.setDepartmentName("总裁办");
        request.setDepartmentEnName("ZCB");
        request.setPriority(10);
        request.setParentId(0);
        long departmentId = enterpriseService.createDepartment(request);
        Assert.assertEquals(true, departmentId > 0);
        //判断同部门名是否可以重复添加
        WarnMessageException warnMessageException = null;
        try {
            long againDepId = enterpriseService.createDepartment(request);
        }catch (WarnMessageException e){
            warnMessageException=e;
        }
        Assert.assertNotNull("重复添加部门未校验",warnMessageException);
        DepartmentInfo departmentInfo = enterpriseService.getDepartment(ENT_ID, departmentId, true);
        Assert.assertEquals(departmentId,departmentInfo.getId());
        Assert.assertEquals("总裁办",departmentInfo.getName());

    }
    @Test
    public void testAddEmpToDep(){
        WarnMessageException exception = null;
        EditDepEmpRequest request = new EditDepEmpRequest();
        //员工判断
        try {

            request.setEnterpriseId(ENT_ID);
            request.setDepartmentId(DEP_ID);
            request.setLeader(true);
            request.setUserId(USER_ID);
            request.setOperatorId(USER_ID);
            enterpriseService.addDepartmentEmployee(request);
        }catch (WarnMessageException e){
            exception=e;
        }
        Assert.assertEquals(FeigeWarn.EMPLOYEE_NOT_EXISTS.code(), exception.getCode());
        //部门判断
        try {
            request.setDepartmentId(DEP_ID + 1);
            enterpriseService.addDepartmentEmployee(request);
        } catch (WarnMessageException e) {
            exception = e;
        }
        Assert.assertEquals(FeigeWarn.DEPARTMENT_NOT_EXISTS.code(), exception.getCode());
        //正常添加
        request.setDepartmentId(DEP_ID);
        enterpriseService.addDepartmentEmployee(request);
        //重复添加判断
        try {
            enterpriseService.addDepartmentEmployee(request);
        } catch (WarnMessageException e) {
            exception = e;
        }
        Assert.assertEquals(FeigeWarn.EMPLOYEE_EXISTS_IN_DEPARTMENT.code(), exception.getCode());
    }
    @Test
    public void testGetDepartment(){
        DepartmentInfo departmentInfo = enterpriseService.getDepartment(ENT_ID, DEP_ID, true);
        System.out.println(departmentInfo.toString());
    }
    @Test
    public void testChildDepartment() {
        CreateDepRequest request = new CreateDepRequest();
        final String departmentName = "总助办公室";
        request.setDepartmentName("总助办公室");
        request.setDepartmentEnName("ZZBGS");
        request.setPriority(10);
        request.setParentId(DEP_ID);
        long departmentId = enterpriseService.createDepartment(request);
        System.out.println("总助办公室部门ID:" + departmentId);
    }
}
