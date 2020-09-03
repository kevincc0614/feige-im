package com.ds.feige.im.test.chat;

import com.ds.feige.im.chat.mapper.UserMessageMapper;
import com.ds.feige.im.chat.po.SenderAndMsg;
import com.ds.feige.im.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DC
 */
public class UserMessageMapperTest extends BaseTest {
    @Autowired
    UserMessageMapper mapper;

    @Test
    public void testFindSenderMessags() {
        List<Long> list = new ArrayList<>();
        list.add(200826955432349696L);
        list.add(2008212036325388288L);
        list.add(2008212036325388288L);
        list.add(2008212036325388288L);
        List<SenderAndMsg> result = mapper.findSenderAndMsgList(1L, list);
        System.out.println(result);
    }
}
