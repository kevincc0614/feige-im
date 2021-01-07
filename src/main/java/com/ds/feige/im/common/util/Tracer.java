package com.ds.feige.im.common.util;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.amqp.core.Message;

import com.ds.feige.im.gateway.socket.protocol.SocketPacket;
import com.google.common.base.Strings;

import cn.hutool.core.util.IdUtil;

/**
 * @author DC
 */
public class Tracer {
    public static final String TRACE_ID_KEY = "traceId";

    public static String getOrGenerateTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (Strings.isNullOrEmpty(traceId)) {
            traceId = IdUtil.fastSimpleUUID();
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    public static String getTraceId(ServletRequest request) {
        String traceId = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest servletRequest = (HttpServletRequest)request;
            traceId = servletRequest.getHeader(TRACE_ID_KEY);
        }
        if (traceId == null) {
            traceId = getOrGenerateTraceId();
        } else {
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    /**
     * 从MQ消息获取traceId,如果没有则生成一个写入本地
     */
    public static String getTraceId(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        String traceId = (String)headers.get(TRACE_ID_KEY);
        if (traceId == null) {
            traceId = getOrGenerateTraceId();
        } else {
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    public static String getTraceId(SocketPacket request) {
        String traceId = null;
        Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            traceId = headers.get(TRACE_ID_KEY);
        }
        if (traceId == null) {
            traceId = getOrGenerateTraceId();
        } else {
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    public static void removeTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }
}
