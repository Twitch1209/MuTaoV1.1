package com.example.cbc.the_hack.entity;

import java.io.Serializable;
import java.util.List;

// 与我相关
public class Relevant implements Serializable {

    private Feed postDetail;
    private Comment commentDetail;
    private Integer replyNum;
    private List<Reply> replyList;

    public Comment getComment() {
        return commentDetail;
    }

    public void setComment(Comment comment) {
        this.commentDetail = comment;
    }

    public Feed getFeed() {
        return postDetail;
    }

    public void setFeed(Feed feed) {
        this.postDetail = feed;
    }

    public Integer getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(Integer replyNum) {
        this.replyNum = replyNum;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }
}