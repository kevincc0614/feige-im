package com.ds.feige.im.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.base.nodepencies.strategy.id.IdKeyGeneratorFactory;

public class JsonTest {
    public static void main(String[] args) throws Exception {

        // SocketRequest request = new SocketRequest();
        // request.setRequestId(1);
        // request.setPath("/chat/message/send");
        // MessageToConversation payload = new MessageToConversation();
        // payload.setTargetId(377665490308518912L);
        // payload.setConversationType(1);
        // payload.setMsgType(0);
        // MessageContent content = new MessageContent.TextMessage("自测消息", null);
        // payload.setMsgContent(content.toJson());
        //
        // request.setPayload(JsonUtils.toJson(payload));
        // System.out.println(request);
        // System.out.println(JsonUtils.toJson(request));
        IdKeyGenerator<Long> keyGeneratorForLong =
            IdKeyGeneratorFactory.instance(IdKeyGeneratorFactory.IdType.WITH_OUT_DATE_LONG);
        long id = keyGeneratorForLong.generateId();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 10, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long epoch = calendar.getTimeInMillis();
        System.out.println(id);
        long timeDuration = id >> 22;
        long max = (long)1 << 41;
        System.out.println(Long.toBinaryString(max));
        System.out.println(max);
        System.out.println(epoch);
        System.out.println(max > epoch);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeDuration + epoch)));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(max)));
    }

}
