package com.ds.feige.im.test.push;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ds.feige.im.test.BaseTest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

/**
 * @author DC
 */
public class PushTest extends BaseTest {
    @Autowired
    FirebaseMessaging firebaseMessaging;

    @Test
    public void testPush() throws Exception {
        Message message = Message.builder().setToken(
            "dQlFmkA2A0VHqBoRPfJj4S:APA91bEDg_cAkiC9HxCWAQo2eUwbDTv8ToQjaN_QSO0T60x2YQ7oyjiw9xsAfJ4sO1IQsQomit89HyUACFvIldwiySaVHtlFrY7zVw_mDCtk8B0EW3ScEwhyIMEr_bkGNKtT3XC_VZly")
            .setNotification(Notification.builder().setTitle("测试标题").setBody("测试内容").build()).build();
        String result = firebaseMessaging.send(message);
        System.out.println(result);
    }
}
