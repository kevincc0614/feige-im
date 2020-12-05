package com.ds.feige.im.enterprise.dto;

import java.util.List;

import com.ds.feige.im.app.dto.AppInfo;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EmpProfile {
    private EmpDetails employee;
    private List<AppInfo> apps;
}
