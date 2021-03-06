package com.ptit.asks.model;

import java.io.Serializable;

public class Answer implements Serializable {
    private int id;
    private int userId;
    private String content;
    private String userName;
    private Boolean voteNumber;
    private String time;

    public Answer(int id, int userId, String content, String userName, Boolean voteNumber, String time) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.userName = userName;
        this.voteNumber = voteNumber;
        this.time = time;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getVoteNumber() {
        return voteNumber;
    }

    public void setVoteNumber(Boolean voteNumber) {
        this.voteNumber = voteNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
