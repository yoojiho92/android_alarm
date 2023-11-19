package com.example.remotebt.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class MediaPlayerManager {
    private static MediaPlayer mediaPlayer;

    public static void playAlarm(Context context, Uri soundUri) {
        stopAlarm(); // 이전 알람이 재생되고 있다면 정지
        mediaPlayer = MediaPlayer.create(context, soundUri);
        mediaPlayer.start();
    }

    public static void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
