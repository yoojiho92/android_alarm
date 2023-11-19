package com.example.remotebt.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.remotebt.util.MediaPlayerManager;

public class StopAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayerManager.stopAlarm();
    }
}