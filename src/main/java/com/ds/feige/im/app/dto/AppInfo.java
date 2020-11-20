package com.ds.feige.im.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author DC
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppInfo {
    private long id;
    private long enterpriseId;
    private String name;
    private String avatar;
    private String config;
    private String secret;
}
