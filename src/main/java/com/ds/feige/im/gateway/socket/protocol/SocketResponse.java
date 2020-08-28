package com.ds.feige.im.gateway.socket.protocol;

import com.ds.base.nodepencies.api.Response;

public class SocketResponse<T> extends Response<T> {
    private static final long serialVersionUID = 3859322325972764845L;
    /**
     * 对应SocketRequest.requestId
     */
    private long responseId;

    public long getResponseId() {
        return responseId;
    }

    public void setResponseId(long responseId) {
        this.responseId = responseId;
    }
}
