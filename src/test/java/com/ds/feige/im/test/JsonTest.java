package com.ds.feige.im.test;

import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.gateway.socket.protocol.SocketRequest;

public class JsonTest {
    public static void main(String[] args) throws Exception{
        String content = "{\n" +
                "    \"requestId\": 1,\n" +
                "    \"path\": \"/chat/message/send\",\n" +
                "    \"headers\": {},\n" +
                "    \"playload\": \"{\\\"targetId\\\":377665490333684736,\\\"conversationType\\\":1,\\\"msgType\\\":0,\\\"sendSeqId\\\":1599572428064,\\\"msgContent\\\":\\\"聊天测试\\\"}\"\n" +
                "}";
        SocketRequest request = JsonUtils.jsonToBean(content, SocketRequest.class);
        System.out.println(request);
    }

}
