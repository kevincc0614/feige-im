package com.ds.feige.im.gateway.socket.protocol;

import java.util.Map;

public class SocketRequest {
    /**消息ID*/
    protected long requestId;
    /**消息path,类似HTTP协议中的URL地址*/
    protected String path;
    /**消息头*/
    protected Map<String,String> headers;
    /**消息体,字符串,内容为JSON体*/
    protected String payload;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "SocketRequest{" +
                "requestId=" + requestId +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                ", payload='" + payload + '\'' +
                '}';
    }
}
