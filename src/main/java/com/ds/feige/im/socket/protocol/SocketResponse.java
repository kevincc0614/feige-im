package com.ds.feige.im.socket.protocol;

import com.ds.base.nodepencies.api.Response;

public class SocketResponse<T> extends Response<T> {
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
