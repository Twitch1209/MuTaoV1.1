package com.example.cbc.the_hack.entity;

import java.io.Serializable;

// 回复
public class Reply implements Serializable {

    private Integer cid;
    private Integer pid;
    private Integer p_cid;
    private User user;
    private User toUser;
    private String content;
    private String create_time;
    private String update_time;

    public Integer getId() {
        return cid;
    }

    public void setId(Integer id) {
        this.cid = id;
    }

    public Integer getFeedId() {
        return pid;
    }

    public void setFeedId(Integer feedId) {
        this.pid = feedId;
    }

    public Integer getCommentId() {
        return p_cid;
    }

    public void setCommentId(Integer commentId) {
        this.p_cid = commentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public String getCommentInfo() {
        return content;
    }

    public void setCommentInfo(String commentInfo) {
        this.content = commentInfo;
    }

    public String getCreateTime() {
        return create_time;
    }

    public void setCreateTime(String createTime) {
        this.create_time = createTime;
    }

    public String getUpdateTime() {
        return update_time;
    }

    public void setUpdateTime(String updateTime) {
        this.update_time = updateTime;
    }
}
