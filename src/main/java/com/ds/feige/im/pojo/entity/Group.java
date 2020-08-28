package com.ds.feige.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_group")
public class Group extends BaseEntity {
    private String name;
    private String avatar;
    private Integer type;
    private Integer maxUserLimit;
    private String announcement;
    private Long announcePubTime;
    private String announcePubUser;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMaxUserLimit() {
        return maxUserLimit;
    }

    public void setMaxUserLimit(Integer maxUserLimit) {
        this.maxUserLimit = maxUserLimit;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Long getAnnouncePubTime() {
        return announcePubTime;
    }

    public void setAnnouncePubTime(Long announcePubTime) {
        this.announcePubTime = announcePubTime;
    }

    public String getAnnouncePubUser() {
        return announcePubUser;
    }

    public void setAnnouncePubUser(String announcePubUser) {
        this.announcePubUser = announcePubUser;
    }
}
