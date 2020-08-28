package com.ds.feige.im.chat.dto.group;

import java.util.Date;

/**
 * 聊天群组信息
 *
 * @author DC
 */
public class GroupInfo {
    private long groupId;
    private String name;
    private String avatar;
    private int type;
    private int maxUserLimit;
    private String announcement;
    private long announcePubUser;
    private Date announcementPubTime;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMaxUserLimit() {
        return maxUserLimit;
    }

    public void setMaxUserLimit(int maxUserLimit) {
        this.maxUserLimit = maxUserLimit;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public long getAnnouncePubUser() {
        return announcePubUser;
    }

    public void setAnnouncePubUser(long announcePubUser) {
        this.announcePubUser = announcePubUser;
    }

    public Date getAnnouncementPubTime() {
        return announcementPubTime;
    }

    public void setAnnouncementPubTime(Date announcementPubTime) {
        this.announcementPubTime = announcementPubTime;
    }
}
