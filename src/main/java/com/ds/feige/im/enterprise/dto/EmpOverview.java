package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EmpOverview {
    private Long userId;
    private String name;
    private String avatar;
    private String title;
    private String workEmail;
}
