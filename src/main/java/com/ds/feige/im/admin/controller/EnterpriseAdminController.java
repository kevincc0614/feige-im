package com.ds.feige.im.admin.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.service.EnterpriseService;
import com.google.common.collect.Sets;

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
        DepartmentBaseInfo departmentInfo = enterpriseService.deleteDepartment(body);
        if (departmentInfo.getCreateGroup() && departmentInfo.getGroupId() != null) {
            groupUserService.disbandGroup(departmentInfo.getGroupId(), body.getOperatorId());
        }
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/employee/list")
    public Response<List<EmployeeInfo>> employeeList(long enterpriseId) {
        List<EmployeeInfo> employeeInfoList = enterpriseService.getEmployees(enterpriseId);
        return new Response(employeeInfoList);
    }

    @RequestMapping("/employee/create")
    public Response createEmployee(HttpServletRequest request, CreateEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.createEmployee(body);
        return Response.EMPTY_SUCCESS;
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
        enterpriseService.editEmployee(body);
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/department/add-employee")
    public Response addDepartmentEmployee(HttpServletRequest request, @RequestBody EditDepEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        enterpriseService.addDepartmentEmployee(body);
        DepartmentDetails departmentDetails =
            enterpriseService.getDepartment(body.getEnterpriseId(), body.getDepartmentId(), false);

        Long groupId = departmentDetails.getGroupId();
        Set<Long> members = Sets.newHashSet();
        // TODO 考虑并发修改的问题
        // 群已创建
        if (groupId != null) {
            members.add(body.getUserId());
            groupUserService.inviteJoinGroup(groupId, members, body.getOperatorId());
        } else {
            Boolean createGroup = departmentDetails.getCreateGroup();
            if (createGroup && departmentDetails.getEmployees().size() >= 3) {
                departmentDetails.getEmployees().forEach(e -> {
                    members.add(e.getUserId());
                });
                GroupInfo newGroup =
                    groupUserService.createGroup(members, departmentDetails.getName(), body.getOperatorId());
                enterpriseService.updateDepartmentGroup(body.getEnterpriseId(), body.getDepartmentId(),
                    newGroup.getGroupId());
            }
        }
        return Response.EMPTY_SUCCESS;
    }

    @RequestMapping("/department/remove-employee")
    public Response removeDepartmentEmployee(HttpServletRequest request, @RequestBody EditDepEmpRequest body) {
        body.setOperatorId(WebUtils.getUserId(request));
        if (enterpriseService.removeDepartmentEmployee(body)) {
            DepartmentDetails departmentDetails =
                enterpriseService.getDepartment(body.getEnterpriseId(), body.getDepartmentId(), false);
            if (departmentDetails.getCreateGroup() && departmentDetails.getGroupId() != null) {
                Set<Long> members = Sets.newHashSet();
                members.add(body.getUserId());
                groupUserService.kickUser(departmentDetails.getGroupId(), members, body.getOperatorId());
            }
        }
        return Response.EMPTY_SUCCESS;
    }
}
