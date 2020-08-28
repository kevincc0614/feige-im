package com.ds.feige.im.test;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.base.nodepencies.strategy.id.IdKeyGeneratorFactory;
import com.ds.feige.im.chat.dto.ChatMsgRequest;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.gateway.socket.protocol.SocketRequest;

public class JsonTest {
    public static void main(String[] args) throws Exception{
        IdKeyGenerator<Long> idKeyGenerator= IdKeyGeneratorFactory.instance(IdKeyGeneratorFactory.IdType.WITH_DATE_SHORT_LONG);
        SocketRequest request=new SocketRequest();
        request.setRequestId(123456l);
        request.setPath("/chat/message/send");
        ChatMsgRequest msgRequest=new ChatMsgRequest();
        msgRequest.setTargetId(2008242178020384768L);
        msgRequest.setConversationType(2);
        msgRequest.setSendSeqId(1);
        msgRequest.setMsgContent("test-message");
        request.setPayload(JsonUtils.toJson(msgRequest));
        System.out.println(JsonUtils.toJson(request));
    }
}
