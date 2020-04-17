package com.example.cbc.the_hack.entity;

/**
 * Created by ABINGCBC
 * on 2020-04-17
 */
public class Poem {
    long id;
    String title;
    String dynasty;
    String author;
    String body;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Poem(long id, String title, String dynasty, String author, String body) {
        this.id = id;
        this.title = title;
        this.dynasty = dynasty;
        this.author = author;
        this.body = body;
    }
}
