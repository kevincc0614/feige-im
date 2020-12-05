package com.ds.feige.im.common.util;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

import com.ds.feige.im.gateway.socket.protocol.SocketPacket;
import com.google.common.base.Strings;

import cn.hutool.core.util.IdUtil;

/**
 * @author DC
 */
public class Tracer {
    public static String setTraceId() {
        String traceId = MDC.get("traceId");
        if (Strings.isNullOrEmpty(traceId)) {
            traceId = IdUtil.fastSimpleUUID();
            MDC.put("traceId", traceId);
        }
        return traceId;
    }

    public static String setTraceId(ServletRequest request) {
        String traceId = MDC.get("traceId");
        if (Strings.isNullOrEmpty(traceId)) {
            if (request instanceof HttpServletRequest) {
                HttpServletRequest servletRequest = (HttpServletRequest)request;
                traceId = servletRequest.getHeader("TraceId");
                if (Strings.isNullOrEmpty(traceId)) {
                    traceId = IdUtil.fastSimpleUUID();
                }
                MDC.put("traceId", traceId);
            }
        }
        return traceId;
    }

    public static String setTraceId(SocketPacket request) {
        String traceId = MDC.get("traceId");
        if (Strings.isNullOrEmpty(traceId)) {
            Map<String, String> headers = request.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                traceId = headers.getOrDefault("traceId", IdUtil.fastSimpleUUID());
            } else {
                traceId = IdUtil.fastSimpleUUID();
            }
            MDC.put("traceId", traceId);
        }
        return traceId;
    }

    public static void removeTraceId() {
        MDC.remove("traceId");
    }
}
