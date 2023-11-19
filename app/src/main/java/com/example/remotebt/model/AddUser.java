package com.example.remotebt.model;

public class AddUser {
    private String add_user = "";
    private String now_pic = "";
    private String complete = "";

    public AddUser(String add_user, String now_pic, String complete){
        this.add_user = add_user;
        this.now_pic = now_pic;
        this.complete = complete;
    }

    public void setAdd_user(String add_user) {
        this.add_user = add_user;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public void setNow_pic(String now_pic) {
        this.now_pic = now_pic;
    }

    public String getAdd_user() {
        return add_user;
    }

    public String getComplete() {
        return complete;
    }

    public String getNow_pic() {
        return now_pic;
    }
}
