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

    public AlarmDAO(int alarm_no, String time, String user, String mealtime){
        this.time = time;
        this.user = user;
        this.mealtime = mealtime;
    }

    public void setAlarm_no(int alarm_no) {
        this.alarm_no = alarm_no;
    }

    public int getAlarm_no() {
        return alarm_no;
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
