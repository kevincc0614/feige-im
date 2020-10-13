package com.ds.feige.im.app.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class AppOperationRequest {
    private String resource;
    private String method;
    private String userOpenId;
}
