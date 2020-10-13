package com.ds.feige.im.enterprise.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
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

    @Override
    public long createEnterprise(String name, String description, long operatorId) {
        // 判断用户是否存在
        UserInfo creator = userService.getUserById(operatorId);
        if (creator == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        Enterprise enterprise = new Enterprise();
        enterprise.setName(name);
        enterprise.setDescription(description);
        enterpriseMapper.insert(enterprise);
        // 创建员工
        Employee employee = new Employee();
        employee.setUserId(creator.getUserId());
        employee.setName(creator.getNickName());
        employee.setRole(EnterpriseRole.SUPER_ADMIN);
        employee.setEnterpriseId(enterprise.getId());
        employeeMapper.insert(employee);
        return enterprise.getId();
    }

    @Override
    public List<EnterpriseInfo> getEnterprises(long userId) {
        List<Enterprise> enterprises = enterpriseMapper.findByUserId(userId);
        return BeansConverter.enterprisesToEnterpriseInfos(enterprises);
    }

    @Override
    public long createDepartment(CreateDepRequest request) {
        // 验证权限
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        long parentId = request.getParentId();
        if (parentId != 0) {
            // 判断上级部门是否存在
            Department parentDep = departmentMapper.selectById(parentId);
            if (parentDep == null) {
                throw new WarnMessageException(FeigeWarn.PARENT_DEPARTMENT_NOT_EXISTS);
            }
        }
        // 判断同级部门中是否存在名称相同的
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
        return department.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DepartmentBaseInfo deleteDepartment(DeleteDepRequest request) {
        long departmentId = request.getDepartmentId();
        long enterpriseId = request.getEnterpriseId();
        // 验证权限
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        log.info("Delete department start:departmentId={}", departmentId);
        // 判断部门是否属于该企业下
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new WarnMessageException(FeigeWarn.DEPARTMENT_NOT_EXISTS);
        }
        if (department.getEnterpriseId() != enterpriseId) {
            throw new WarnMessageException(FeigeWarn.PERMISSION_DIED);
        }
        // 判断是有还有下级部门,如果有下级部门,则不能直接删除
        List<DepartmentDetails> childs = departmentMapper.findByParentId(enterpriseId, request.getDepartmentId());
        if (childs != null && !childs.isEmpty()) {
            throw new WarnMessageException(FeigeWarn.HAS_CHILD_DEPS_CAN_NOT_DELETE);
        }
        // 删除部门数据
        departmentMapper.deleteById(departmentId);
        // 解除员工和部门的关系
        departmentEmployeeMapper.deleteByDepartmentId(departmentId);
        log.info("Delete department success:departmentId={}", departmentId);
        return BeansConverter.departmentToSimpleDepartmentInfo(department);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void editEmployee(EditEmpRequest request) {
        log.info("Edit employee start:request={}", request.toString());
        final long userId = request.getUserId();
        // 判断是否具备权限
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        Employee employee = employeeMapper.getByUserId(request.getEnterpriseId(), request.getUserId());
        if (employee == null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_NOT_EXISTS);
        }
        // TODO 一个企业只能有一个超级管理员
        // 更新员工基本信息
        employee.setRole(request.getRole());
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
        // 判断是否具备权限
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
    public void addDepartmentEmployee(EditDepEmpRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long userId = request.getUserId();
        long departmentId = request.getDepartmentId();
        boolean leader = request.isLeader();
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        // 判断员工是否存在
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        if (employee == null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_NOT_EXISTS);
        }
        // 判断部门是否存在
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new WarnMessageException(FeigeWarn.DEPARTMENT_NOT_EXISTS);
        }
        // 判断员工是否在对应部门已存在
        DepartmentEmployee count = departmentEmployeeMapper.getByUserAndDepartmentId(userId, departmentId);
        if (count != null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_EXISTS_IN_DEPARTMENT);
        }
        DepartmentEmployee departmentEmployee = new DepartmentEmployee();
        department.setEnterpriseId(enterpriseId);
        departmentEmployee.setDepartmentId(departmentId);
        departmentEmployee.setUserId(userId);
        departmentEmployee.setLeader(leader);
        departmentEmployeeMapper.insert(departmentEmployee);

    }

    @Override
    public boolean removeDepartmentEmployee(EditDepEmpRequest request) {
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        int i = departmentEmployeeMapper.deleteDepartmentEmployee(request.getEnterpriseId(), request.getDepartmentId(),
            request.getUserId());
        log.info("Delete department employee success:request={}", request);
        return i == 1;
    }

    @Override
    public DepartmentDetails getDepartment(long enterpriseId, long departmentId, boolean queryChild) {
        DepartmentDetails info = new DepartmentDetails();
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            return null;
        }
        BeanUtils.copyProperties(department, info);
        if (queryChild) {
            // 查询子部门
            List<DepartmentDetails> child = departmentMapper.findByParentId(enterpriseId, departmentId);
            info.setDepartments(child);
        }
        List<EmployeeInfo> employeeInfos = employeeMapper.findByDepartmentId(enterpriseId, departmentId);
        info.setEmployees(employeeInfos);
        return info;
    }

    @Override
    public List<DepartmentBaseInfo> getDepartments(long enterpriseId) {
        List<Department> departments = departmentMapper.findByEnterpriseId(enterpriseId);
        return BeansConverter.departmentsToSimpleDepartmentInfos(departments);
    }

    @Override
    public long createEmployee(CreateEmpRequest request) {
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        // 判断用户是否存在
        UserInfo userInfo = userService.getUserById(request.getUserId());
        if (userInfo == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        // 判断员工是否已存在
        Employee employee = employeeMapper.getByUserId(request.getEnterpriseId(), request.getUserId());
        if (employee != null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_IS_EXISTS);
        }
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
        return employee.getId();
    }

    @Override
    public void deleteEmployee(DeleteEmpRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long userId = request.getUserId();
        checkAdmin(request.getEnterpriseId(), request.getOperatorId());
        // 删除员工表数据
        employeeMapper.deleteEmployee(enterpriseId, userId);
        // 删除部门关联数据
        departmentEmployeeMapper.deleteByUserId(enterpriseId, userId);
        log.info("Delete employee success:enterpriseId={},userId={}", enterpriseId, userId);
    }

    @Override
    public EmployeeInfo getEmployeeByUserId(long enterpriseId, long userId) {
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        List<Long> departmentIds = departmentEmployeeMapper.findDepartments(userId);
        EmployeeInfo employeeInfo = new EmployeeInfo();
        BeanUtils.copyProperties(employee, employeeInfo);
        employeeInfo.setDepartments(departmentIds);
        return employeeInfo;
    }

    @Override
    public List<EmployeeInfo> getEmployees(long enterpriseId) {
        List<Employee> employees = employeeMapper.findByEnterpriseId(enterpriseId);
        return BeansConverter.empsToEmpInfos(employees);
    }

    @Override
    public boolean isAdmin(long enterpriseId, long userId) {
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        if (employee == null) {
            return false;
        }
        String role = employee.getRole();
        // 角色权限校验
        return EnterpriseRole.ADMIN.equals(role) || EnterpriseRole.SUPER_ADMIN.equals(role);
    }

    @Override
    public void checkAdmin(long enterpriseId, long userId) {
        // 验证权限
        if (!isAdmin(enterpriseId, userId)) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_ROLE_NOT_ADMIN);
        }
    }
}
