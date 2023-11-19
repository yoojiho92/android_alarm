package com.example.remotebt.model;

public class AlarmDAO {
    private int alarm_no;
    private String time;
    private String user;
    private String mealtime;

    public AlarmDAO(String time, String user, String mealtime){
        this.time = time;
        this.user = user;
        this.mealtime = mealtime;
    }

    public String getUser() {
        return user;
    }

    public String getMealtime() {
        return mealtime;
    }

    public String getTime() {
        return time;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMealtime(String mealtime) {
        this.mealtime = mealtime;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
