package com.ds.feige.im.test.enterprise;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.pojo.dto.enterprise.CreateDepartmentRequest;
import com.ds.feige.im.pojo.dto.enterprise.CreateEmployeeRequest;
import com.ds.feige.im.pojo.dto.enterprise.DepartmentInfo;
import com.ds.feige.im.pojo.dto.enterprise.EmployeeInfo;
import com.ds.feige.im.pojo.dto.user.UserInfo;
import com.ds.feige.im.service.enterprise.EnterpriseService;
import com.ds.feige.im.service.user.UserService;
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
    static final long DEP_ID=2008171236913319936L;
    @Test
    public void testCreateEmp(){
        UserInfo userInfo=userService.getUserById(USER_ID);
        CreateEmployeeRequest request=new CreateEmployeeRequest();
        request.setUserId(userInfo.getUserId());
        request.setName(userInfo.getNickName());
        request.setTitle("CEO");
        request.setWorkEmail(userInfo.getEmail());
        long employeeId=enterpriseService.createEmployee(request);
        Assert.assertEquals(true,employeeId>0);
        EmployeeInfo employeeInfo=enterpriseService.getEmployeeByUserId(userInfo.getUserId());
        Assert.assertEquals(userInfo.getUserId(),employeeInfo.getUserId());
        System.out.println("员工用户ID:"+employeeInfo.getUserId());
    }
    @Test
    public void testCreateDepartment(){
        CreateDepartmentRequest request=new CreateDepartmentRequest();
        final String departmentName="总裁办";
        request.setDepartmentName("总裁办");
        request.setDepartmentEnName("ZCB");
        request.setPriority(10);
        request.setParentId(0);
        long departmentId=enterpriseService.createDepartment(request);
        Assert.assertEquals(true,departmentId>0);
        //判断同部门名是否可以重复添加
        WarnMessageException warnMessageException=null;
        try{
            long againDepId=enterpriseService.createDepartment(request);
        }catch (WarnMessageException e){
            warnMessageException=e;
        }
        Assert.assertNotNull("重复添加部门未校验",warnMessageException);
        DepartmentInfo departmentInfo=enterpriseService.getDepartment(departmentId,true);
        Assert.assertEquals(departmentId,departmentInfo.getId());
        Assert.assertEquals("总裁办",departmentInfo.getName());

    }
    @Test
    public void testAddEmpToDep(){
        WarnMessageException exception=null;
        //员工判断
        try{
            enterpriseService.addEmployeeToDepartment(USER_ID+1,DEP_ID,true,0l);
        }catch (WarnMessageException e){
            exception=e;
        }
        Assert.assertEquals(FeigeWarn.EMPLOYEE_NOT_EXISTS.code(),exception.getCode());
        //部门判断
        try{
            enterpriseService.addEmployeeToDepartment(USER_ID,DEP_ID+1,true,0l);
        }catch (WarnMessageException e){
            exception=e;
        }
        Assert.assertEquals(FeigeWarn.DEPARTMENT_NOT_EXISTS.code(),exception.getCode());
        //正常添加
        enterpriseService.addEmployeeToDepartment(USER_ID,DEP_ID,true,0l);
        //重复添加判断
        try{
            enterpriseService.addEmployeeToDepartment(USER_ID,DEP_ID,true,0l);
        }catch (WarnMessageException e){
            exception=e;
        }
        Assert.assertEquals(FeigeWarn.EMPLOYEE_EXISTS_IN_DEPARTMENT.code(),exception.getCode());
    }
    @Test
    public void testGetDepartment(){
        DepartmentInfo departmentInfo=enterpriseService.getDepartment(DEP_ID,true);
        System.out.println(departmentInfo.toString());
    }
    @Test
    public void testChildDepartment(){
        CreateDepartmentRequest request=new CreateDepartmentRequest();
        final String departmentName="总助办公室";
        request.setDepartmentName("总助办公室");
        request.setDepartmentEnName("ZZBGS");
        request.setPriority(10);
        request.setParentId(DEP_ID);
        long departmentId=enterpriseService.createDepartment(request);
        System.out.println("总助办公室部门ID:"+departmentId);
    }
}
