package com.ds.feige.im.enterprise.service;

import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.app.service.AppService;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.constants.EnterpriseRole;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.entity.Department;
import com.ds.feige.im.enterprise.entity.DepartmentEmployee;
import com.ds.feige.im.enterprise.entity.Employee;
import com.ds.feige.im.enterprise.entity.Enterprise;
import com.ds.feige.im.enterprise.mapper.DepartmentEmployeeMapper;
import com.ds.feige.im.enterprise.mapper.DepartmentMapper;
import com.ds.feige.im.enterprise.mapper.EmployeeMapper;
import com.ds.feige.im.enterprise.mapper.EnterpriseMapper;
import com.ds.feige.im.enterprise.po.DepEmpPo;
import com.ds.feige.im.enterprise.po.EmpDepBindingPo;
import com.ds.feige.im.gateway.domain.UserState;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnterpriseServiceImpl implements EnterpriseService {
    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    DepartmentEmployeeMapper departmentEmployeeMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    EnterpriseMapper enterpriseMapper;
    @Autowired
    UserService userService;
    @Autowired
    SessionUserService sessionUserService;
    @Autowired
    private GroupUserService groupUserService;
    @Autowired
    AppService appService;

    @Override
    public long createEnterprise(String name, String description, long createUserId) {
        // ????????????????????????
        UserInfo creator = userService.getUserById(createUserId);
        if (creator == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        Enterprise enterprise = new Enterprise();
        enterprise.setName(name);
        enterprise.setDescription(description);
        enterpriseMapper.insert(enterprise);
        // ????????????
        Employee employee = new Employee();
        employee.setUserId(creator.getUserId());
        employee.setName(creator.getNickName());
        employee.setRole(EnterpriseRole.SUPER_ADMIN);
        employee.setEnterpriseId(enterprise.getId());
        employeeMapper.insert(employee);
        log.info("Create enterprise success:enterprise={},creator={}", enterprise, employee);
        return enterprise.getId();
    }

    @Override
    public List<EnterpriseInfo> getEnterprises(long userId) {
        List<Enterprise> enterprises = enterpriseMapper.findByUserId(userId);
        return BeansConverter.enterprisesToEnterpriseInfos(enterprises);
    }

    @Override
    public long createDepartment(CreateDepRequest request) {
        // ????????????
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        long parentId = request.getParentId();
        if (parentId != 0) {
            // ??????????????????????????????
            Department parentDep = departmentMapper.selectById(parentId);
            if (parentDep == null) {
                throw new WarnMessageException(FeigeWarn.PARENT_DEPARTMENT_NOT_EXISTS);
            }
        }
        // ????????????????????????????????????????????????
        int existsCount =
            departmentMapper.countSameNameDepartment(request.getEnterpriseId(), parentId, request.getDepartmentName());
        if (existsCount > 0) {
            throw new WarnMessageException(FeigeWarn.DEPARTMENT_NAME_EXISTS);
        }
        Department department = new Department();
        department.setName(request.getDepartmentName());
        department.setEnName(request.getDepartmentEnName());
        department.setParentId(parentId);
        department.setPriority(request.getPriority());
        department.setEnterpriseId(request.getEnterpriseId());
        departmentMapper.insert(department);
        log.info("Create department success:department={}", department);
        return department.getId();
    }

    @Override
    public DepartmentOverview deleteDepartment(DeleteDepRequest request) {
        long departmentId = request.getDepartmentId();
        long enterpriseId = request.getEnterpriseId();
        // ????????????
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        log.info("Delete department start:departmentId={}", departmentId);
        // ????????????????????????????????????
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new WarnMessageException(FeigeWarn.DEPARTMENT_NOT_EXISTS);
        }
        if (department.getEnterpriseId() != enterpriseId) {
            throw new WarnMessageException(FeigeWarn.PERMISSION_DIED);
        }
        // ??????????????????????????????,?????????????????????,?????????????????????
        List<DepartmentDetails> childs = departmentMapper.findByParentId(enterpriseId, request.getDepartmentId());
        if (childs != null && !childs.isEmpty()) {
            throw new WarnMessageException(FeigeWarn.HAS_CHILD_DEPS_CAN_NOT_DELETE);
        }
        // ??????????????????
        departmentMapper.deleteById(departmentId);
        // ??????????????????????????????
        departmentEmployeeMapper.deleteByDepartmentId(departmentId);
        log.info("Delete department success:departmentId={}", departmentId);
        return BeansConverter.convertToDepOverview(department);
    }

    @Override
    public void editEmp(EditEmpRequest request) {
        log.info("Edit employee start:request={}", request.toString());
        final long userId = request.getUserId();
        // ????????????????????????
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        Employee employee = employeeMapper.getByUserId(request.getEnterpriseId(), request.getUserId());
        if (employee == null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_NOT_EXISTS);
        }
        // ????????????????????????
        // employee.setRole(request.getRoles());
        employee.setEmployeeNo(request.getEmployeeNo());
        employee.setTitle(request.getTitle());
        employee.setWorkEmail(request.getWorkEmail());
        employee.setName(request.getName());
        employeeMapper.updateById(employee);
        log.info("Edit employee success:request={}", request.toString());
    }

    @Override
    public void editDepartment(EditDepRequest request) {
        log.info("Edit department start:request={}", request);
        // ????????????????????????
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        Department department = new Department();
        department.setEnName(request.getEnName());
        department.setId(request.getDepartmentId());
        department.setPriority(request.getPriority());
        department.setName(request.getName());
        department.setParentId(request.getParentId());
        departmentMapper.updateById(department);
        log.info("Edit department success:request={}", request);
    }

    @Override
    public void updateDepartmentGroup(long enterpriseId, long departmentId, long groupId) {
        int i = departmentMapper.updateGroupId(enterpriseId, departmentId, groupId);
        log.info("Update department group:departmentId={},groupId={}", departmentId, groupId);
    }

    @Override
    public void editEmpDepartments(EditDepEmpRequest request) {
        log.info("Start edit employee departments:{}", request);
        long enterpriseId = request.getEnterpriseId();
        long userId = request.getUserId();
        Map<Long, Boolean> newDepartments = request.getDepartments();
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        // ????????????????????????
        List<DepartmentEmployee> oldDeps = departmentEmployeeMapper.findUserDepartments(enterpriseId, userId);
        // ????????????,???????????????????????????????????????
        Set<Long> remove = Sets.newHashSet();
        oldDeps.forEach(de -> {
            // ????????????,?????????????????????????????????
            long departmentId = de.getDepartmentId();
            if (newDepartments != null) {
                if (!newDepartments.containsKey(de.getDepartmentId())) {
                    remove.add(de.getId());
                    removeGroupUser(enterpriseId, departmentId, userId, request.getOperatorId());
                }
            } else {
                remove.add(de.getId());
                removeGroupUser(enterpriseId, departmentId, userId, request.getOperatorId());
            }
        });
        if (remove != null && !remove.isEmpty()) {
            departmentEmployeeMapper.deleteBatchIds(remove);
        }
        // ?????????????????????
        for (Map.Entry<Long, Boolean> entry : newDepartments.entrySet()) {
            long departmentId = entry.getKey();
            boolean leader = entry.getValue() == null ? false : entry.getValue();
            DepartmentEmployee de =
                departmentEmployeeMapper.getByDepartmentIdAndUserId(enterpriseId, departmentId, userId);
            // ???????????????,?????????
            if (de == null) {
                de = new DepartmentEmployee();
                de.setEnterpriseId(enterpriseId);
                de.setUserId(userId);
                de.setDepartmentId(departmentId);
                departmentEmployeeMapper.insert(de);
                addUserToGroup(enterpriseId, departmentId, userId, request.getOperatorId());
            } else {
                // ???????????????????????????,??????????????????
                if (leader != de.getLeader()) {
                    de.setLeader(leader);
                    departmentEmployeeMapper.updateById(de);
                }

            }
        }

    }

    public void removeGroupUser(long enterpriseId, long departmentId, long userId, long operatorId) {
        DepartmentDetails departmentDetails = getDepartment(enterpriseId, departmentId, false);
        if (departmentDetails.getCreateGroup() && departmentDetails.getGroupId() != null) {
            Set<Long> members = Sets.newHashSet();
            members.add(userId);
            groupUserService.kickUser(departmentDetails.getGroupId(), members, operatorId);
        }
    }

    public void addUserToGroup(long enterpriseId, long departmentId, long userId, long operatorId) {
        DepartmentDetails departmentDetails = getDepartment(enterpriseId, departmentId, false);
        Long groupId = departmentDetails.getGroupId();
        Set<Long> members = Sets.newHashSet();
        // TODO ???????????????????????????
        // ?????????????????????
        // ????????????
        if (groupId != null) {
            members.add(userId);
            groupUserService.inviteJoinGroup(groupId, members, operatorId);
        } else {
            Boolean createGroup = departmentDetails.getCreateGroup();
            if (createGroup && departmentDetails.getEmployees().size() >= 3) {
                departmentDetails.getEmployees().forEach(e -> {
                    members.add(e.getUserId());
                });
                GroupInfo newGroup = groupUserService.createGroup(members, departmentDetails.getName(), operatorId);
                updateDepartmentGroup(enterpriseId, departmentId, newGroup.getGroupId());
            }
        }
    }

    @Override
    public DepartmentDetails getDepartment(long enterpriseId, long departmentId, boolean queryChild) {
        DepartmentDetails departmentDetails = new DepartmentDetails();
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            return null;
        }
        BeanUtils.copyProperties(department, departmentDetails);
        if (queryChild) {
            // ???????????????
            List<DepartmentDetails> child = departmentMapper.findByParentId(enterpriseId, departmentId);
            departmentDetails.setDepartments(child);
        }
        List<EmpDetails> empDetails = employeeMapper.findByDepartmentId(enterpriseId, departmentId);
        departmentDetails.setEmployees(empDetails);
        Collection<Long> onlineUsers = sessionUserService.getOnlineUsers(empDetails);
        departmentDetails.setOnline(onlineUsers.size());
        departmentDetails.setTotal(empDetails.size());
        return departmentDetails;
    }

    @Override
    public List<DepartmentOverview> getDepartments(long enterpriseId) {
        List<Department> departments = departmentMapper.findByEnterpriseId(enterpriseId);
        return BeansConverter.convertToDepOverviews(departments);
    }

    @Override
    public long createEmp(AddEmpRequest request) {
        log.info("Create employee start:request={}", request);
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        // ????????????????????????
        UserInfo userInfo = userService.getUserById(request.getUserId());
        if (userInfo == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        // ???????????????????????????
        Employee employee = employeeMapper.getByUserId(request.getEnterpriseId(), request.getUserId());
        if (employee != null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_IS_EXISTS);
        }
        // ?????????????????????
        employee = new Employee();
        employee.setEnterpriseId(request.getEnterpriseId());
        employee.setUserId(request.getUserId());
        employee.setName(request.getName() == null ? userInfo.getNickName() : request.getName());
        employee.setAvatar(userInfo.getAvatar());
        employee.setEmployeeNo(request.getEmployeeNo());
        employee.setTitle(request.getTitle());
        employee.setRole(request.getRole());
        employee.setWorkEmail(request.getWorkEmail());
        employeeMapper.insert(employee);
        log.info("Create employee success:employee={}", employee);
        return employee.getId();
    }

    @Override
    public void deleteEmp(DeleteEmpRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long userId = request.getUserId();
        long operatorId = request.getOperatorId();
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        if (employee == null) {
            log.warn("Employee not exists:enterpriseId={},userId={}", enterpriseId, userId);
            return;
        }
        // ???????????????????????????,??????????????????????????????????????????
        String role = employee.getRole();
        if (EnterpriseRole.SUPER_ADMIN.equals(role)) {
            throw new WarnMessageException(FeigeWarn.CAN_NOT_DELETE_SUPER_ADMIN);
        }
        checkAdmin(enterpriseId, operatorId);
        // ?????????????????????
        employeeMapper.deleteById(employee.getId());
        // ????????????????????????
        departmentEmployeeMapper.deleteByUserId(enterpriseId, userId);
        log.info("Delete employee success:enterpriseId={},userId={}", enterpriseId, userId);
    }

    @Override
    public EmpDetails getEmpDetails(long enterpriseId, long userId) {
        // ??????????????????
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        if (employee == null) {
            return null;
        }
        List<EmpDepBindingPo> departmentBindings = departmentEmployeeMapper.findEmpDepBindings(enterpriseId, userId);
        EmpDetails empDetails = new EmpDetails();
        BeanUtils.copyProperties(employee, empDetails);
        // ????????????
        List<EmpDetails.DepKeyInfo> departments = Lists.newArrayList();
        departmentBindings.forEach(e -> {
            EmpDetails.DepKeyInfo keyInfo = new EmpDetails.DepKeyInfo();
            keyInfo.setDepartmentId(e.getDepartmentId());
            keyInfo.setDepartmentName(e.getDepartmentName());
            keyInfo.setLeader(e.getLeader());
            departments.add(keyInfo);
        });
        empDetails.setDepartments(departments);
        UserState userState = sessionUserService.getSessionUser(userId).getState();
        empDetails.setState(userState);
        return empDetails;
    }

    @Override
    public List<EmpDetails> getAllEmpDetailList(long enterpriseId, Collection<Long> excludeUsers) {
        List<DepEmpPo> employees = employeeMapper.findyEntId(enterpriseId);
        List<EmpDetails> list = new ArrayList<>();
        Map<Long, UserState> entUserStates = sessionUserService.getUserStates(employees);
        Map<Long, EmpDetails> detailsMap = Maps.newHashMap();
        employees.forEach(e -> {
            if (excludeUsers != null && !excludeUsers.isEmpty() && excludeUsers.contains(e.getUserId())) {
                return;
            }
            EmpDetails details = detailsMap.get(e.getUserId());
            if (details == null) {
                details = BeansConverter.convertToDetails(e);
                detailsMap.put(e.getUserId(), details);
            }
            EmpDetails.DepKeyInfo depKeyInfo = new EmpDetails.DepKeyInfo();
            depKeyInfo.setDepartmentName(e.getDepartmentName());
            depKeyInfo.setDepartmentId(e.getDepartmentId());
            depKeyInfo.setLeader(e.getLeader());
            details.getDepartments().add(depKeyInfo);
            details.setState(entUserStates.get(e.getUserId()));
            list.add(details);
        });
        return list;
    }

    private boolean isAdmin(Employee employee) {
        String role = employee.getRole();
        // ??????????????????
        return EnterpriseRole.ADMIN.equals(role) || EnterpriseRole.SUPER_ADMIN.equals(role);
    }

    @Override
    public boolean isAdmin(long enterpriseId, long userId) {
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        if (employee == null) {
            return false;
        }
        return isAdmin(employee);
    }

    @Override
    public void checkAdmin(long enterpriseId, long userId) {
        // ????????????
        if (!isAdmin(enterpriseId, userId)) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_ROLE_NOT_ADMIN);
        }
    }
}
