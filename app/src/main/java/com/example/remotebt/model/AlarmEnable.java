package com.example.remotebt.model;

public class AlarmEnable {

    private String enable ="";
    private String mealTime ="";
    private String user ="";
    public AlarmEnable(String enable, String mealTime, String user){
        this.enable = enable;
        this.mealTime = mealTime;
        this.user = user;
    }
    public String getEnable() {
        return enable;
    }

    public String getMealTime() {
        return mealTime;
    }

    public String getUser() {
        return user;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
