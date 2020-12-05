package com.ds.feige.im.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.entity.User;
import com.ds.feige.im.app.dto.AppInfo;
import com.ds.feige.im.app.entity.App;
import com.ds.feige.im.chat.dto.MessageToUser;
import com.ds.feige.im.chat.dto.event.ConversationMessageEvent;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.entity.Group;
import com.ds.feige.im.enterprise.dto.*;
import com.ds.feige.im.enterprise.entity.Department;
import com.ds.feige.im.enterprise.entity.EmpRole;
import com.ds.feige.im.enterprise.entity.Employee;
import com.ds.feige.im.enterprise.entity.Enterprise;
import com.ds.feige.im.event.dto.UserEventInfo;
import com.ds.feige.im.event.entity.UserEvent;
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
        if (user == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setUserId(user.getId());
        return userInfo;
    }

    public static List<UserInfo> usersToUserInfos(Collection<User> users) {
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

    public static EmpDetails convertToDetails(Employee employee) {
        EmpDetails empDetails = new EmpDetails();
        BeanUtils.copyProperties(employee, empDetails);
        return empDetails;
    }

    public static List<EmpDetails> empsToEmpInfos(List<Employee> employees) {
        List<EmpDetails> result = new ArrayList<>(employees.size());
        employees.forEach(e -> result.add(convertToDetails(e)));
        return result;
    }

    public static List<EmpOverview> convertToEmpOverviews(List<Employee> employees) {
        List<EmpOverview> result = new ArrayList<>(employees.size());
        employees.forEach(e -> result.add(convertToEmpOverview(e)));
        return result;
    }

    public static EmpOverview convertToEmpOverview(Employee employee) {
        EmpOverview overview = new EmpOverview();
        BeanUtils.copyProperties(employee, overview);
        return overview;
    }

    public static DepartmentOverview convertToDepOverview(Department department) {
        DepartmentOverview departmentDetails = new DepartmentOverview();
        BeanUtils.copyProperties(department, departmentDetails);
        return departmentDetails;
    }

    public static List<DepartmentOverview> convertToDepOverviews(List<Department> departments) {
        List<DepartmentOverview> result = new ArrayList<>(departments.size());
        departments.forEach(department -> result.add(convertToDepOverview(department)));
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

    public static AppInfo convertToAppInfo(App app) {
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(app, appInfo);
        return appInfo;
    }


    public static EmpRoleInfo convertToEmpRoleInfo(EmpRole empRole) {
        EmpRoleInfo info = new EmpRoleInfo();
        BeanUtils.copyProperties(empRole, info);
        return info;
    }

    public static List<EmpRoleInfo> convertToEmpRoleInfos(List<EmpRole> sources) {
        if (sources == null) {
            return null;
        }
        List<EmpRoleInfo> result = new ArrayList<>(sources.size());
        sources.forEach(source -> result.add(convertToEmpRoleInfo(source)));
        return result;
    }

    public static UserEventInfo convertToUserEventInfo(UserEvent userEvent) {
        UserEventInfo userEventInfo = new UserEventInfo();
        BeanUtils.copyProperties(userEvent, userEventInfo);
        return userEventInfo;
    }

    public static List<UserEventInfo> convertToUserEventInfos(Collection<UserEvent> events) {
        if (events == null) {
            return null;
        }
        List<UserEventInfo> result = new ArrayList<>();
        events.forEach(e -> {
            result.add(convertToUserEventInfo(e));
        });
        return result;
    }
}
