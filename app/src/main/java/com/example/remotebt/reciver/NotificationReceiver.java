package com.example.remotebt.reciver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.remotebt.AlarmActivity;
import com.example.remotebt.util.MediaPlayerManager;
import com.example.remotebt.R;

public class NotificationReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();

    NotificationManager manager;
    NotificationCompat.Builder builder;
    MediaPlayer mediaPlayer;
    Context ctx;


    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        MediaPlayerManager.playAlarm(context, alert); // 알람 소리 재생




        //showDialog();
        Log.e(TAG, "onReceive 알람이 들어옴!!");

        String contentValue = intent.getStringExtra("content");
        String mealTime = intent.getStringExtra("mealtime");
        String user = intent.getStringExtra("user");
        Log.e(TAG, "onReceive contentValue값 확인 : " + contentValue);
        Log.e(TAG, "onReceive mealTime값 확인 : " + mealTime);
        Log.e(TAG, "onReceive user값 확인 : " + user);

        builder = null;

        //푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);


            // 알림 소리 설정
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes);
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);

        }

        //알림창 클릭 시 지정된 activity 화면으로 이동
        Intent intent2 = new Intent(context, AlarmActivity.class);
        intent2.putExtra("content", "알람리스트 테스트");
        intent2.putExtra("mealtime", mealTime);
        intent2.putExtra("user", user);

        // FLAG_UPDATE_CURRENT ->
        // 설명된 PendingIntent가 이미 존재하는 경우 유지하되, 추가 데이터를 이 새 Intent에 있는 것으로 대체함을 나타내는 플래그입니다.
        // getActivity, getBroadcast 및 getService와 함께 사용
        PendingIntent pendingIntent = PendingIntent.getActivity(context,101,intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(context, StopAlarmReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.addAction(R.drawable.baseline_access_alarm_24, "정지", stopPendingIntent);

        //알림창 제목
        builder.setContentTitle(contentValue); //회의명노출
        //builder.setContentText(intent.getStringExtra("content")); //회의 내용
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.baseline_access_alarm_24);
        //알림창 터치시 자동 삭제
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        //푸시알림 빌드
        Notification notification = builder.build();

        //NotificationManager를 이용하여 푸시 알림 보내기
        manager.notify(1,notification);

    }
}