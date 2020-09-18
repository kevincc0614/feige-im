package com.ds.feige.im.test.enterprise;

import com.ds.feige.im.enterprise.dto.EditDepEmpRequest;
import com.ds.feige.im.test.BaseTest;
import org.junit.Test;

public class EnterpriseServiceTest extends BaseTest {
    @Test
    public void testCreateEnt() {
        long entId = enterpriseService.createEnterprise("梦幻热游", "梦幻热游", user_ids.get(0));
        System.out.println(entId);
    }

    @Test
    public void testCreateEmp() {
//        UserInfo userInfo = userService.getUserById(USER_ID);
//        CreateEmpRequest request = new CreateEmpRequest();
//        request.setUserId(userInfo.getUserId());
//        request.setName(userInfo.getNickName());
//        request.setTitle("CEO");
//        request.setWorkEmail(userInfo.getEmail());
//        long employeeId = enterpriseService.createEmployee(request);
//        Assert.assertEquals(true, employeeId > 0);
//        EmployeeInfo employeeInfo = enterpriseService.getEmployeeByUserId(ENT_ID, userInfo.getUserId());
//        Assert.assertEquals(userInfo.getUserId(), employeeInfo.getUserId());
//        System.out.println("员工用户ID:" + employeeInfo.getUserId());
    }
//    @Test
//    public void testCreateDepartment() {
//        CreateDepRequest request = new CreateDepRequest();
//        final String depName = "小组1";
//        request.setDepartmentName(depName);
//        request.setDepartmentEnName("xz1");
//        request.setPriority(10);
////        request.setParentId(PARENT_DEP_ID);
////        request.setEnterpriseId(ENT_ID);
////        request.setOperatorId(USER_ID);
//        long departmentId = enterpriseService.createDepartment(request);
//        Assert.assertEquals(true, departmentId > 0);
//        //判断同部门名是否可以重复添加
//        WarnMessageException warnMessageException = null;
//        try {
//            long againDepId = enterpriseService.createDepartment(request);
//        } catch (WarnMessageException e) {
//            warnMessageException = e;
//        }
//        Assert.assertNotNull("重复添加部门未校验", warnMessageException);
//        DepartmentInfo departmentInfo = enterpriseService.getDepartment(ENT_ID, departmentId, true);
//        Assert.assertEquals(departmentId, departmentInfo.getId());
//        Assert.assertEquals(depName, departmentInfo.getName());
//
//    }
//
//    @Test
//    public void testDeleteDepartment() {
////        DeleteDepRequest request = new DeleteDepRequest();
////        request.setOperatorId(USER_ID);
////        request.setEnterpriseId(ENT_ID);
////        request.setDepartmentId(PARENT_DEP_ID);
////        WarnMessageException warn = null;
////        try {
////            enterpriseService.deleteDepartment(request);
////        } catch (WarnMessageException e) {
////            warn = e;
////        }
////        Assert.assertNotNull(FeigeWarn.HAS_CHILD_DEPS_CAN_NOT_DELETE.code(), warn.getCode());
////        request.setDepartmentId(DEP_ID);
////        enterpriseService.deleteDepartment(request);
//    }
//
@Test
public void testAddEmpToDep() {
    EditDepEmpRequest request = new EditDepEmpRequest();
    request.setUserId(377665490283353088L);
    request.setDepartmentId(377665490476291072L);
    request.setEnterpriseId(377665490446930944L);
    request.setLeader(true);
    request.setOperatorId(377665490283353088L);
    enterpriseService.addDepartmentEmployee(request);
}
//    @Test
//    public void testGetDepartment(){
//        DepartmentInfo departmentInfo = enterpriseService.getDepartment(ENT_ID, DEP_ID, true);
//        System.out.println(departmentInfo.toString());
//    }
//    @Test
//    public void testChildDepartment() {
//        CreateDepRequest request = new CreateDepRequest();
//        final String departmentName = "总助办公室";
//        request.setDepartmentName("总助办公室");
//        request.setDepartmentEnName("ZZBGS");
//        request.setPriority(10);
//        request.setParentId(DEP_ID);
//        long departmentId = enterpriseService.createDepartment(request);
//        System.out.println("总助办公室部门ID:" + departmentId);
//    }
}
