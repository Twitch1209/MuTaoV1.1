package com.example.cbc.the_hack.entity;

import java.io.Serializable;

// 点赞
public class Like implements Serializable {

    private Integer userId;
    private String username;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
