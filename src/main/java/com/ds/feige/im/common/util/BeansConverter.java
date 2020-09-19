package com.ds.feige.im.common.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.entity.User;
import com.ds.feige.im.chat.dto.MessageToUser;
import com.ds.feige.im.chat.dto.UserConversationInfo;
import com.ds.feige.im.chat.dto.event.ConversationMessageEvent;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.entity.Group;
import com.ds.feige.im.chat.entity.UserConversation;
import com.ds.feige.im.enterprise.dto.DepartmentInfo;
import com.ds.feige.im.enterprise.dto.EmployeeInfo;
import com.ds.feige.im.enterprise.dto.EnterpriseInfo;
import com.ds.feige.im.enterprise.dto.SimpleDepartmentInfo;
import com.ds.feige.im.enterprise.entity.Department;
import com.ds.feige.im.enterprise.entity.Employee;
import com.ds.feige.im.enterprise.entity.Enterprise;
import com.ds.feige.im.mark.dto.MarkMessageInfo;
import com.ds.feige.im.mark.entity.MarkMessage;
import com.ds.feige.im.oss.dto.UploadCompleteRequest;
import com.ds.feige.im.oss.entity.OSSFile;

/**
 * Bean转换工具类
 *
 * @author DC
 */
public class BeansConverter {
    public static UserInfo userToUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setUserId(user.getId());
        return userInfo;
    }

    public static List<UserInfo> usersToUserInfos(List<User> users) {
        List<UserInfo> result = new ArrayList<>(users.size());
        users.forEach(u -> result.add(userToUserInfo(u)));
        return result;
    }

    public static GroupInfo groupToGroupInfo(Group group) {
        GroupInfo groupInfo = new GroupInfo();
        BeanUtils.copyProperties(group, groupInfo);
        groupInfo.setGroupId(group.getId());
        return groupInfo;
    }

    public static MessageToUser conversationMsgToMessageToUser(ConversationMessageEvent message) {
        MessageToUser chatMessage = new MessageToUser();
        BeanUtils.copyProperties(message, chatMessage);
        return chatMessage;
    }

    public static OSSFile uploadCompleteToOSSFile(UploadCompleteRequest request) {
        OSSFile ossFile = new OSSFile();
        BeanUtils.copyProperties(request, ossFile);
        return ossFile;
    }

    public static EnterpriseInfo enterpriseToEnterpriseInfo(Enterprise enterprise) {
        EnterpriseInfo result = new EnterpriseInfo();
        BeanUtils.copyProperties(enterprise, result);
        return result;
    }

    public static List<EnterpriseInfo> enterprisesToEnterpriseInfos(List<Enterprise> enterprises) {
        List<EnterpriseInfo> result = new ArrayList<>(enterprises.size());
        enterprises.forEach(e -> result.add(enterpriseToEnterpriseInfo(e)));
        return result;
    }

    public static EmployeeInfo employeeToEmployeeInfo(Employee employee) {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        BeanUtils.copyProperties(employee, employeeInfo);
        return employeeInfo;
    }

    public static List<EmployeeInfo> empsToEmpInfos(List<Employee> employees) {
        List<EmployeeInfo> result = new ArrayList<>(employees.size());
        employees.forEach(e -> result.add(employeeToEmployeeInfo(e)));
        return result;
    }

    public static SimpleDepartmentInfo departmentToSimpleDepartmentInfo(Department department) {
        DepartmentInfo departmentInfo = new DepartmentInfo();
        BeanUtils.copyProperties(department, departmentInfo);
        return departmentInfo;
    }

    public static List<SimpleDepartmentInfo> departmentsToSimpleDepartmentInfos(List<Department> departments) {
        List<SimpleDepartmentInfo> result = new ArrayList<>(departments.size());
        departments.forEach(department -> result.add(departmentToSimpleDepartmentInfo(department)));
        return result;
    }

    public static UserConversationInfo conversationToConversationInfo(UserConversation conversation) {
        UserConversationInfo info = new UserConversationInfo();
        BeanUtils.copyProperties(conversation, info);
        return info;
    }

    public static List<UserConversationInfo> conversationToConversationInfo(List<UserConversation> conversations) {
        List<UserConversationInfo> result = new ArrayList<>(conversations.size());
        conversations.forEach(c -> result.add(conversationToConversationInfo(c)));
        return result;
    }

    public static MarkMessageInfo markMessageToMarkMessageInfo(MarkMessage markMessage) {
        MarkMessageInfo result = new MarkMessageInfo();
        BeanUtils.copyProperties(markMessage, result);
        return result;
    }

    public static List<MarkMessageInfo> markMessagesToMarkMessageInfos(List<MarkMessage> markMessages) {
        List<MarkMessageInfo> result = new ArrayList<>(markMessages.size());
        markMessages.forEach(m -> result.add(markMessageToMarkMessageInfo(m)));
        return result;
    }
}
