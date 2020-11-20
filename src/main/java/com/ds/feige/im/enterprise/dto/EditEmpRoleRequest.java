package com.ds.feige.im.enterprise.dto;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EditEmpRoleRequest extends EnterpriseOpRequest {
    /** 为NULL表示新增,不为null表示修改 */
    private Long roleId;
    @NotBlank
    private String roleName;
    @Size(min = 1, max = 100, message = "权限最少一个,最多100个")
    private Set<Long> authorityIds;
}
