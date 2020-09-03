package com.ds.feige.im.enterprise.service;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.constants.EmployeeRole;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.entity.Department;
import com.ds.feige.im.enterprise.entity.DepartmentEmployee;
import com.ds.feige.im.enterprise.entity.Employee;
import com.ds.feige.im.enterprise.entity.Enterprise;
import com.ds.feige.im.enterprise.mapper.DepartmentEmployeeMapper;
import com.ds.feige.im.enterprise.mapper.DepartmentMapper;
import com.ds.feige.im.enterprise.mapper.EmployeeMapper;
import com.ds.feige.im.enterprise.mapper.EnterpriseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {
    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    DepartmentEmployeeMapper departmentEmployeeMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    EnterpriseMapper enterpriseMapper;
    static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseServiceImpl.class);

    @Override
    public List<EnterpriseInfo> getEnterprises(long userId) {
        List<Enterprise> enterprises = enterpriseMapper.findByUserId(userId);
        return BeansConverter.enterprisesToEnterpriseInfos(enterprises);
    }

    @Override
    public long createDepartment(CreateDepRequest request) {
        //验证权限
        if (!isAdmin(request.getEnterpriseId(), request.getOperatorId())) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_ROLE_NOT_ADMIN);
        }
        long parentId = request.getParentId();
        if (parentId != 0) {
            //判断上级部门是否存在
            Department parentDep = departmentMapper.selectById(parentId);
            if (parentDep == null) {
                throw new WarnMessageException(FeigeWarn.PARENT_DEPARTMENT_NOT_EXISTS);
            }
        }
        //判断同级部门中是否存在名称相同的
        int existsCount = departmentMapper.countByParentIdAndName(parentId, request.getDepartmentName());
        if (existsCount > 0) {
            throw new WarnMessageException(FeigeWarn.DEPARTMENT_NAME_EXISTS);
        }
        Department department = new Department();
        department.setName(request.getDepartmentName());
        department.setEnName(request.getDepartmentEnName());
        department.setParentId(parentId);
        department.setPriority(request.getPriority());
        departmentMapper.insert(department);
        return department.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteDepartment(DeleteDepRequest request) {
        long departmentId = request.getDepartmentId();
        long enterpriseId = request.getEnterpriseId();
        //验证权限
        if (!isAdmin(request.getEnterpriseId(), request.getOperatorId())) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_ROLE_NOT_ADMIN);
        }
        LOGGER.info("Delete department start:departmentId={}", departmentId);
        //判断部门是否属于该企业下
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new WarnMessageException(FeigeWarn.DEPARTMENT_NOT_EXISTS);
        }
        if (department.getEnterpriseId() != enterpriseId) {
            throw new WarnMessageException(FeigeWarn.PERMISSION_DIED);
        }
        //删除部门数据
        departmentMapper.deleteById(departmentId);
        //解除员工和部门的关系
        departmentEmployeeMapper.deleteByDepartmentId(departmentId);
        LOGGER.info("Delete department success:departmentId={}", departmentId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void editEmployee(EditEmpRequest request) {
        LOGGER.info("Edit employee start:request={}", request.toString());
        final long userId = request.getUserId();
        //判断是否具备权限
        if (!isAdmin(request.getEnterpriseId(), request.getOperatorId())) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_ROLE_NOT_ADMIN);
        }
        Employee employee = employeeMapper.getByUserId(request.getEnterpriseId(), request.getUserId());
        if (employee == null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_NOT_EXISTS);
        }
        //TODO 一个企业只能有一个超级管理员
        //更新员工基本信息
        employee.setRole(request.getRole());
        employee.setEmployeeNo(request.getEmployeeNo());
        employee.setTitle(request.getTitle());
        employee.setWorkEmail(request.getWorkEmail());
        employee.setName(request.getName());
        employeeMapper.updateById(employee);
        LOGGER.info("Edit employee success:request={}", request.toString());
    }

    @Override
    public void editDepartment(EditDepRequest request) {
        LOGGER.info("Edit department start:request={}", request);
        //判断是否具备权限
        if (!isAdmin(request.getEnterpriseId(), request.getOperatorId())) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_ROLE_NOT_ADMIN);
        }
        Department department = new Department();
        department.setEnName(request.getEnName());
        department.setId(request.getDepartmentId());
        department.setPriority(request.getPriority());
        department.setName(request.getName());
        department.setParentId(request.getParentId());
        departmentMapper.updateById(department);
        LOGGER.info("Edit department success:request={}", request);
    }

    @Override
    public void addDepartmentEmployee(EditDepEmpRequest request) {
        long enterpriseId = request.getEnterpriseId();
        long userId = request.getUserId();
        long departmentId = request.getDepartmentId();
        boolean leader = request.isLeader();
        if (!isAdmin(request.getEnterpriseId(), request.getOperatorId())) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_ROLE_NOT_ADMIN);
        }
        //判断员工是否存在
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        if (employee == null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_NOT_EXISTS);
        }
        //判断部门是否存在
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new WarnMessageException(FeigeWarn.DEPARTMENT_NOT_EXISTS);
        }
        //判断员工是否在对应部门已存在
        DepartmentEmployee count = departmentEmployeeMapper.getOne(userId, departmentId);
        if (count != null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_EXISTS_IN_DEPARTMENT);
        }
        DepartmentEmployee departmentEmployee = new DepartmentEmployee();
        departmentEmployee.setDepartmentId(departmentId);
        departmentEmployee.setUserId(userId);
        departmentEmployee.setLeader(leader);
        departmentEmployeeMapper.insert(departmentEmployee);

    }

    @Override
    public void removeDepartmentEmployee(EditDepEmpRequest request) {
        departmentEmployeeMapper.deleteDepartmentEmployee(request.getEnterpriseId(), request.getDepartmentId(), request.getUserId());
        LOGGER.info("Delete department employee success:request={}", request);
    }

    @Override
    public DepartmentInfo getDepartment(long enterpriseId, long departmentId, boolean queryChild) {
        DepartmentInfo info = new DepartmentInfo();
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            return null;
        }
        BeanUtils.copyProperties(department, info);
        if (queryChild) {
            //查询子部门
            List<DepartmentInfo> child = departmentMapper.findByParentId(departmentId);
            info.setDepartments(child);
        }
        List<EmployeeInfo> employeeInfos = employeeMapper.findByDepartmentId(enterpriseId, departmentId);
        info.setEmployees(employeeInfos);
        return info;
    }

    @Override
    public long createEmployee(CreateEmpRequest request) {
        //判断员工是否已存在
        Employee employee = employeeMapper.getByUserId(request.getEnterpriseId(), request.getUserId());
        if (employee != null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_IS_EXISTS);
        }
        employee = new Employee();
        employee.setUserId(request.getUserId());
        employee.setName(request.getName());
        employee.setEmployeeNo(request.getEmployeeNo());
        employee.setTitle(request.getTitle());
        employee.setRole(request.getRole());
        employee.setWorkEmail(request.getWorkEmail());
        employeeMapper.insert(employee);
        return employee.getId();
    }

    @Override
    public EmployeeInfo getEmployeeByUserId(long enterpriseId, long userId) {
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        EmployeeInfo employeeInfo = new EmployeeInfo();
        BeanUtils.copyProperties(employee, employeeInfo);
        return employeeInfo;
    }

    @Override
    public List<EmployeeInfo> getEmployees(long enterpriseId) {
        return null;
    }

    @Override
    public boolean isAdmin(long enterpriseId, long userId) {
        Employee employee = employeeMapper.getByUserId(enterpriseId, userId);
        if (employee == null) {
            return false;
        }
        String role = employee.getRole();
        //角色权限校验
        return EmployeeRole.ADMIN.equals(role) && EmployeeRole.SUPER_ADMIN.equals(role);
    }
}