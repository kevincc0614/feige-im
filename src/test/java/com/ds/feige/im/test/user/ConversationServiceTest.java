package com.ds.feige.im.test.user;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.chat.dto.UserConversationInfo;
import com.ds.feige.im.chat.service.ConversationService;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConversationServiceTest extends BaseTest {
    @Autowired
    ConversationService conversationService;

    @Test
    public void testGetOrCreateConversation(){
        Long senderId=2008141140050280448L;
        Long targetId=2008141150478262272L;
        UserConversationInfo conversationInfo=conversationService.getOrCreateConversation(senderId,targetId, ConversationType.SINGLE_CONVERSATION_TYPE);
        Assert.assertEquals(senderId,conversationInfo.getUserId());
        Assert.assertEquals(targetId,conversationInfo.getTargetId());
    }
    @Test
    public void testNoExistsUserCreateConversation(){
        Long senderId=0L;
        Long targetId=0L;
        try{
            UserConversationInfo conversationInfo=conversationService.getOrCreateConversation(senderId,targetId, ConversationType.SINGLE_CONVERSATION_TYPE);
        }catch (WarnMessageException e){
            Assert.assertEquals("用户不存在,不能创建会话", FeigeWarn.USER_NOT_EXISTS.code(),e.getCode());
        }
    }
}
