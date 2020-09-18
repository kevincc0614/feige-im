package com.ds.feige.im.test.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.feige.im.chat.dto.*;
import com.ds.feige.im.chat.service.ChatService;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.MsgType;
import com.ds.feige.im.test.BaseTest;

public class ChatServiceTest extends BaseTest {
    @Autowired
    ChatService chatService;
    static final long USER_ID_1=2008141150478262272L;
    static final long USER_ID_2=2008141140050280448L;
    @Test
    public void testSend(){
        for(int i=0;i<100;i++){
            MessageToConversation request = new MessageToConversation();
            if(i%2==0){
                request.setUserId(USER_ID_1);
                request.setTargetId(USER_ID_2);
                request.setMsgContent("U1->U2 :"+i);
            }else{
                request.setUserId(USER_ID_2);
                request.setTargetId(USER_ID_1);
                request.setMsgContent("U2->U1 :"+i);
            }
            request.setConversationType(ConversationType.SINGLE_CONVERSATION_TYPE);
            request.setMsgType(MsgType.TEXT);
            MessageToUser result = chatService.sendToConversation(request);
            System.out.println("发送消息结果:"+result.toString());
        }

    }
    @Test
    public void testPullMessage() throws Exception{
        ConversationMessageQueryRequest request = new ConversationMessageQueryRequest();
        request.setConversationId(2008141888938344448l);
        request.setMaxMsgId(Long.MAX_VALUE);
        request.setPageSize(2);
        request.setUserId(USER_ID_1);
        List<MessageToUser> chatMessageList = chatService.pullMessages(request);
        System.out.println(JsonUtils.toJson(chatMessageList));
//        for(ChatMessage msg: chatMessageList){
//            System.out.println("发送者:"+msg.getSenderId());
//            System.out.println("消息内容:"+msg.getMsgContent());
//        }
//        Assert.assertEquals(0, chatMessageList.size());

    }

    @Test
    public void testAck() {
        ConversationMessageQueryRequest request = new ConversationMessageQueryRequest();
        request.setConversationId(2008141888938344448l);
        request.setMaxMsgId(Long.MAX_VALUE);
        request.setPageSize(100);
        request.setUserId(USER_ID_1);
        List<MessageToUser> chatMessageList = chatService.pullMessages(request);
        List<Long> msgIds = new ArrayList<>();
        for (MessageToUser msg : chatMessageList) {
            System.out.println("发送者:" + msg.getSenderId());
            System.out.println("消息内容:" + msg.getMsgContent());
            msgIds.add(msg.getMsgId());
        }
        ChatMessageAckResult result = chatService.ackMessages(USER_ID_1, msgIds);
        System.out.println(result);
    }
    @Test
    public void testChatPreview() throws Exception{
        Collection<ConversationPreview> previews = chatService.getConversationPreviews(377665490283353088L);
        System.out.println(JsonUtils.toJson(previews));
    }
}
