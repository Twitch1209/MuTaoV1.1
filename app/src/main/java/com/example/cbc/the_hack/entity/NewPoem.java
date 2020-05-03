package com.example.cbc.the_hack.entity;

import java.io.Serializable;

/**
 * @author ZTL
 */
public class NewPoem implements Serializable {
    private String poetryBody;
    private String poetryDynasty;
    private String poetryName;
    private String poetryAuthor;
    private Integer isAI;

    public String getPoetryBody() {
        return poetryBody;
    }

    public void setPoetryBody(String poetryBody) {
        this.poetryBody = poetryBody;
    }

    public String getPoetryDynasty() {
        return poetryDynasty;
    }

    public void setPoetryDynasty(String poetryDynasty) {
        this.poetryDynasty = poetryDynasty;
    }

    public String getPoetryName() {
        return poetryName;
    }

    public void setPoetryName(String poetryName) {
        this.poetryName = poetryName;
    }

    public String getPoetryAuthor() {
        return poetryAuthor;
    }

    public void setPoetryAuthor(String poetryAuthor) {
        this.poetryAuthor = poetryAuthor;
    }

    public Integer getIsAI() {
        return isAI;
    }

    public void setIsAI(Integer isAI) {
        this.isAI = isAI;
    }
}
