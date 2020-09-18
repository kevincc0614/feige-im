package com.ds.feige.im.test;

import com.ds.feige.im.chat.dto.MessageToConversation;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.gateway.socket.protocol.SocketRequest;

public class JsonTest {
    public static void main(String[] args) throws Exception {

        SocketRequest request = new SocketRequest();
        request.setRequestId(1);
        request.setPath("/chat/message/send");
        MessageToConversation payload = new MessageToConversation();
        payload.setTargetId(377665490308518912L);
        payload.setConversationType(1);
        payload.setMsgType(0);
        payload.setMsgContent("Chrome测试消息");

        request.setPayload(JsonUtils.toJson(payload));
        System.out.println(request);
        System.out.println(JsonUtils.toJson(request));
    }

}
