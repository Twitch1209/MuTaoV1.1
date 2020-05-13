package com.example.cbc.the_hack.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

// 动态
public class Feed implements Serializable {

    private Integer pid;
    // 用户信息
    private User user;
    private String content;
    private Integer view_num;
    // 评论数
    private Integer commentNum;
    // 当前用户是否点赞
    private boolean like;
    // 点赞列表
    private List<Like> likeList;
    // 相册
    private List<String> photoList;
    private String create_time;
    private String update_time;

    public Integer getId() {
        return pid;
    }

    public void setId(Integer id) {
        this.pid = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFeedInfo() {
        return content;
    }

    public void setFeedInfo(String feedInfo) {
        this.content = feedInfo;
    }

    public Integer getViewNum() {
        return view_num;
    }

    public void setViewNum(Integer viewNum) {
        this.view_num = viewNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public List<Like> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<Like> likeList) {
        this.likeList = likeList;
    }

    public List<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
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


    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getView_num() {
        return view_num;
    }

    public void setView_num(Integer view_num) {
        this.view_num = view_num;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "pid='" + pid + '\'' +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", view_num=" + view_num +
                ", commentNum=" + commentNum +
                ", like=" + like +
                ", likeList=" + likeList +
                ", photoList=" + photoList +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                '}';
    }
}
