package com.ds.feige.im.common.web;

import javax.servlet.http.HttpServletRequest;

/**
 * @author DC
 */
public class WebUtils {
    public static Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }
}
