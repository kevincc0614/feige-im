package com.ds.feige.im.chat.dto;

import com.ds.feige.im.chat.po.UnreadMessagePreview;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class ConversationPreview extends UnreadMessagePreview {
    private MessageToUser lastMsg;
    private int conversationType;
}
