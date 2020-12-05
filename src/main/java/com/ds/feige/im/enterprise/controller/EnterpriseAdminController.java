package com.ds.feige.im.enterprise.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.service.EnterpriseSecurityService;
import com.ds.feige.im.enterprise.service.EnterpriseService;
import com.google.common.collect.Maps;

/**
 * 企业管理
 *
 * @author DC
 */
@RequestMapping("/admin/enterprise")
@RestController
public class EnterpriseAdminController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    UserService userService;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    EnterpriseSecurityService enterpriseSecurityService;
    @PostMapping("/create")
    public Response createEnterprise(HttpServletRequest request, @RequestBody @Valid CreateEntRequest body) {
        long userId = WebUtils.getUserId(request);
        long enterpriseId = enterpriseService.createEnterprise(body.getName(), body.getDescription(), userId);
        return new Response(enterpriseId);
    }
    @RequestMapping("/department/info")
    public Response<DepartmentDetails> departmentInfo(@RequestBody GetDepRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long departmentId = request.getDepartmentId();
        boolean isQueryChild = request.isQueryChild();
        DepartmentDetails departmentDetails = enterpriseService.getDepartment(enterpriseId, departmentId, isQueryChild);
        return new Response<>(departmentDetails);
    }

    @RequestMapping("/department/create")
    public Response<Long> createDepartment(HttpServletRequest request, @RequestBody CreateDepRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        long departmentId = enterpriseService.createDepartment(body);
        return new Response<>(departmentId);
    }

    @RequestMapping("/department/delete")
    public Response deleteDepartment(HttpServletRequest request, @RequestBody DeleteDepRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        DepartmentOverview departmentOverview = enterpriseService.deleteDepartment(body);
        if (departmentOverview.getCreateGroup() && departmentOverview.getGroupId() != null) {
            groupUserService.disbandGroup(departmentOverview.getGroupId());
        }
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/employee/list")
    public Response<List<EmpDetails>> employeeList(long enterpriseId) {
        List<EmpDetails> overviews = enterpriseService.getAllEmpDetailList(enterpriseId, null);
        return new Response(overviews);
    }

    @RequestMapping("/employee/create")
    public Response createEmployee(HttpServletRequest request, @RequestBody @Valid EditEmpRequest body) {
        long enterpriseId = body.getEnterpriseId();
        long operatorId = WebUtils.getUserId(request);
        body.setOperatorId(operatorId);
        // 接口上为UI简单化,暂时只变更单个部门
        Map<Long, Boolean> departments = Maps.newHashMap();
        departments.put(body.getDepartmentId(), body.getLeader());
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setLoginName(body.getLoginName());
        registerRequest.setPassword(body.getPassword());
        registerRequest.setNickName(body.getName());
        registerRequest.setSource("enterprise_admin");
        // 注册用户
        long userId = userService.register(registerRequest);
        AddEmpRequest addEmpRequest = new AddEmpRequest();
        addEmpRequest.setUserId(userId);
        addEmpRequest.setName(body.getName());
        addEmpRequest.setEmployeeNo(body.getEmployeeNo());
        addEmpRequest.setTitle(body.getTitle());
        addEmpRequest.setWorkEmail(body.getWorkEmail());
        addEmpRequest.setOperatorId(operatorId);
        addEmpRequest.setEnterpriseId(enterpriseId);
        addEmpRequest.setDepartments(departments);
        // 添加员工
        long employeeId = enterpriseService.createEmp(addEmpRequest);
        // 员工部门关系表添加数据
        EditDepEmpRequest editDepEmpRequest = new EditDepEmpRequest();
        editDepEmpRequest.setEnterpriseId(enterpriseId);
        editDepEmpRequest.setOperatorId(operatorId);
        editDepEmpRequest.setDepartments(departments);
        editDepEmpRequest.setUserId(userId);
        enterpriseService.editEmpDepartments(editDepEmpRequest);
        // 分配角色
        EditEmpRoleBindingRequest roleBinding = new EditEmpRoleBindingRequest();
        roleBinding.setEnterpriseId(enterpriseId);
        roleBinding.setOperatorId(operatorId);
        roleBinding.setUserId(userId);
        roleBinding.setRoles(body.getRoles());
        enterpriseSecurityService.editEmpRoleBindings(roleBinding);
        return new Response(employeeId);
    }

    @RequestMapping("/department/edit")
    public Response editDepartment(HttpServletRequest request, @RequestBody EditDepRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.editDepartment(body);
        // TODO 变更群聊
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/employee/edit")
    public Response editEmployee(HttpServletRequest request, @RequestBody EditEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        // 更新账户信息
        userService.updateUser(body.getUserId(), body.getLoginName(), body.getPassword());
        // 更新员工信息
        enterpriseService.editEmp(body);
        EditDepEmpRequest editDepEmpRequest = new EditDepEmpRequest();
        // 接口上为UI简单化,暂时只变更单个部门
        Map<Long, Boolean> departments = Maps.newHashMap();
        departments.put(body.getDepartmentId(), body.getLeader());
        editDepEmpRequest.setDepartments(departments);
        editDepEmpRequest.setUserId(body.getUserId());
        editDepEmpRequest.setEnterpriseId(body.getEnterpriseId());
        editDepEmpRequest.setOperatorId(body.getOperatorId());
        // 更新部门信息
        enterpriseService.editEmpDepartments(editDepEmpRequest);
        // 分配角色
        EditEmpRoleBindingRequest roleBinding = new EditEmpRoleBindingRequest();
        roleBinding.setEnterpriseId(body.getEnterpriseId());
        roleBinding.setOperatorId(body.getOperatorId());
        roleBinding.setUserId(body.getUserId());
        roleBinding.setRoles(body.getRoles());
        enterpriseSecurityService.editEmpRoleBindings(roleBinding);
        return Response.EMPTY_SUCCESS;
    }

    // TODO 删除员工
    @RequestMapping("/employee/delete")
    public Response deleteEmployee(HttpServletRequest request, @RequestBody @Valid DeleteEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.deleteEmp(body);
        enterpriseSecurityService.deleteEmpAllRoles(body.getEnterpriseId(), body.getUserId());
        // 删除账号
        userService.deleteUser(body.getUserId());
        return Response.EMPTY_SUCCESS;
    }
    // @RequestMapping("/department/add-employee")
    // public Response addDepartmentEmployee(HttpServletRequest request, @RequestBody EditDepEmpRequest body) {
    // body.setOperatorId(WebUtils.getUserId(request));
    // enterpriseService.addDepartmentEmployee(body);
    // return Response.EMPTY_SUCCESS;
    // }

    // @RequestMapping("/department/remove-employee")
    // public Response removeDepartmentEmployee(HttpServletRequest request, @RequestBody EditDepEmpRequest body) {
    // body.setOperatorId(WebUtils.getUserId(request));
    // if (enterpriseService.removeDepartmentEmployee(body)) {
    //
    // }
    // return Response.EMPTY_SUCCESS;
    // }
}
