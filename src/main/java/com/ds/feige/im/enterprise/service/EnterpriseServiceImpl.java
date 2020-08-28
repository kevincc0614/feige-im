package com.ds.feige.im.enterprise.service;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.dto.CreateDepartmentRequest;
import com.ds.feige.im.enterprise.dto.CreateEmployeeRequest;
import com.ds.feige.im.enterprise.dto.DepartmentInfo;
import com.ds.feige.im.enterprise.dto.EmployeeInfo;
import com.ds.feige.im.enterprise.entity.Department;
import com.ds.feige.im.enterprise.entity.DepartmentEmployee;
import com.ds.feige.im.enterprise.entity.Employee;
import com.ds.feige.im.enterprise.mapper.DepartmentEmployeeMapper;
import com.ds.feige.im.enterprise.mapper.DepartmentMapper;
import com.ds.feige.im.enterprise.mapper.EmployeeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {
    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    DepartmentEmployeeMapper departmentEmployeeMapper;
    @Autowired
    EmployeeMapper employeeMapper;

    @Override
    public long createDepartment(CreateDepartmentRequest request) {
        //TODO 验证权限,判断用户是否存在,判断用户是否具备操作权限
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

    @Override
    public void addEmployeeToDepartment(long userId, long departmentId, boolean leader, long operatorId) {
        //TODO 操作权限验证
        //判断员工是否存在
        Employee employee = employeeMapper.getByUserId(userId);
        if (employee == null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_NOT_EXISTS);
        }
        //判断部门是否存在
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new WarnMessageException(FeigeWarn.DEPARTMENT_NOT_EXISTS);
        }
        //判断员工是否在对应部门已存在
        int count = departmentEmployeeMapper.countByUserIdAndDepartmentId(userId, departmentId);
        if (count > 0) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_EXISTS_IN_DEPARTMENT);
        }
        DepartmentEmployee departmentEmployee = new DepartmentEmployee();
        departmentEmployee.setDepartmentId(departmentId);
        departmentEmployee.setUserId(userId);
        departmentEmployee.setLeader(leader);
        departmentEmployeeMapper.insert(departmentEmployee);

    }

    @Override
    public DepartmentInfo getDepartment(long departmentId, boolean queryChild) {
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
        List<EmployeeInfo> employeeInfos = employeeMapper.findByDepartmentId(departmentId);
        info.setEmployees(employeeInfos);
        return info;
    }

    @Override
    public long createEmployee(CreateEmployeeRequest request) {
        //判断员工是否已存在
        Employee employee = employeeMapper.getByUserId(request.getUserId());
        if (employee != null) {
            throw new WarnMessageException(FeigeWarn.EMPLOYEE_IS_EXISTS);
        }
        employee = new Employee();
        employee.setUserId(request.getUserId());
        employee.setName(request.getName());
        employee.setEmployeeNo(request.getEmployeeNo());
        employee.setTitle(request.getTitle());
        employee.setRoleId(request.getRoleId());
        employee.setWorkEmail(request.getWorkEmail());
        employeeMapper.insert(employee);
        return employee.getId();
    }

    @Override
    public EmployeeInfo getEmployeeByUserId(long userId) {
        Employee employee = employeeMapper.getByUserId(userId);
        EmployeeInfo employeeInfo = new EmployeeInfo();
        BeanUtils.copyProperties(employee, employeeInfo);
        return employeeInfo;
    }
}
