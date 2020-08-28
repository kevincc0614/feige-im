package com.ds.feige.im.test.chat;

import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.MsgType;
import com.ds.feige.im.pojo.dto.ChatMsgRequest;
import com.ds.feige.im.pojo.dto.chat.ChatMessage;
import com.ds.feige.im.pojo.dto.chat.ChatMsgAckResult;
import com.ds.feige.im.pojo.dto.chat.ConversationPreview;
import com.ds.feige.im.pojo.dto.chat.PullConversationMsgRequest;
import com.ds.feige.im.pojo.dto.message.SendMsgResult;
import com.ds.feige.im.service.chat.ChatService;
import com.ds.feige.im.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ChatServiceTest extends BaseTest {
    @Autowired
    ChatService chatService;
    static final long USER_ID_1=2008141150478262272L;
    static final long USER_ID_2=2008141140050280448L;
    @Test
    public void testSend(){
        for(int i=0;i<100;i++){
            ChatMsgRequest request=new ChatMsgRequest();
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
            SendMsgResult result=chatService.sendMsg(request);
            System.out.println("发送消息结果:"+result.toString());
        }

    }
    @Test
    public void testPullMessage() throws Exception{
        PullConversationMsgRequest request=new PullConversationMsgRequest();
        request.setConversationId(2008141888938344448l);
        request.setMaxMsgId(Long.MAX_VALUE);
        request.setPageSize(2);
        request.setUserId(USER_ID_1);
        List<ChatMessage> chatMessageList =chatService.pullMsg(request);
        System.out.println(JsonUtils.toJson(chatMessageList));
//        for(ChatMessage msg: chatMessageList){
//            System.out.println("发送者:"+msg.getSenderId());
//            System.out.println("消息内容:"+msg.getMsgContent());
//        }
//        Assert.assertEquals(0, chatMessageList.size());

    }
    @Test
    public void testAck(){
        PullConversationMsgRequest request=new PullConversationMsgRequest();
        request.setConversationId(2008141888938344448l);
        request.setMaxMsgId(Long.MAX_VALUE);
        request.setPageSize(100);
        request.setUserId(USER_ID_1);
        List<ChatMessage> chatMessageList =chatService.pullMsg(request);
        List<Long> msgIds=new ArrayList<>();
        for(ChatMessage msg: chatMessageList){
            System.out.println("发送者:"+msg.getSenderId());
            System.out.println("消息内容:"+msg.getMsgContent());
            msgIds.add(msg.getMsgId());
        }
        ChatMsgAckResult result=chatService.ackMsg(USER_ID_1,msgIds);
        System.out.println(result);
    }
    @Test
    public void testChatPreview() throws Exception{
        List<ConversationPreview> previews=chatService.getConversationPreviews(USER_ID_1);
        System.out.println(JsonUtils.toJson(previews));
    }
}
