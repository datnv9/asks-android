package com.ptit.asks.model;

import java.io.Serializable;

public class Question implements Serializable {
    private int id;
    private String title;
    private String username;
    private String content;
    private int vote;
    private int userid;
    private String date;
    private int solve;

    public Question(int id, String title, String content, String userName, int userId, int vote, String date, int solve) {
        this.id = id;
        this.title = title;
        this.username = userName;
        this.userid = userId;
        this.vote = vote;
        this.date = date;
        this.solve = solve;
        this.content = content;
    }

    public int isSolve() {
        return solve;
    }

    public void setSolve(int solve) {
        this.solve = solve;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getUserId() {
        return userid;
    }

    public void setUserId(int vote) {
        this.userid = userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}


