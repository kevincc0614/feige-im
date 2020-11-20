package com.ds.feige.im.enterprise.dto;

import java.util.Set;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EditEmpRoleBindingRequest extends EnterpriseOpRequest {
    @Positive
    private Long userId;
    @Size(min = 1, max = 500)
    private Set<Long> roles;

}
