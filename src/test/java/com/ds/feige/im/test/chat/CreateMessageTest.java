package com.ds.feige.im.test.chat;

import com.ds.feige.im.chat.dto.MessageContent;
import com.ds.feige.im.chat.dto.MessageToConversation;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.MsgType;
import com.ds.feige.im.gateway.socket.protocol.SocketPacket;

/**
 * @author DC
 */
public class CreateMessageTest {
    public static void main(String[] args) throws Exception {
        SocketPacket packet = new SocketPacket();
        packet.setPath("/chat/message/send");
        packet.setRequestId(1);
        MessageToConversation messageToConversation = new MessageToConversation();
        MessageContent.TextMessage textMessage = new MessageContent.TextMessage("测试", null);
        messageToConversation.setMsgContent(textMessage.toJson());
        messageToConversation.setTargetId(406355803890966528L);
        messageToConversation.setMsgType(MsgType.TEXT);
        messageToConversation.setConversationType(1);
        packet.setPayload(JsonUtils.toJson(messageToConversation));
        System.out.println(JsonUtils.toJson(packet));
    }
}
