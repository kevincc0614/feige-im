package com.ds.feige.im.gateway.socket.protocol;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties
@Data
public class SocketRequest {
    /** 消息ID */
    protected long requestId;
    /** 消息path,类似HTTP协议中的URL地址 */
    protected String path;
    /** 消息头 */
    protected Map<String, String> headers;
    /** 消息体,字符串,内容为JSON体 */
    protected String payload;
}
