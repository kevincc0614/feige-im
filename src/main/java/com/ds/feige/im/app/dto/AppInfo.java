package com.ds.feige.im.app.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class AppInfo {
    private Long id;
    private Long enterpriseId;
    private String name;
    private String avatar;
    private String secret;

}
