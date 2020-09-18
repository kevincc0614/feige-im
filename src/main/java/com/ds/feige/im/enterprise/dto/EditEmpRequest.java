package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EditEmpRequest extends EnterpriseOpRequest {
    private long userId;
    private String name;
    private String employeeNo;
    private String title;
    private String workEmail;
    private String role;
}
