package com.example.remotebt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.remotebt.model.AlarmEnable;
import com.example.remotebt.util.MediaPlayerManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private String user ="";
    private String mealTime ="";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaPlayerManager.stopAlarm();
        Intent intent = getIntent();

        String contentValue = intent.getStringExtra("content");
        mealTime = intent.getStringExtra("mealtime");
        user = intent.getStringExtra("user");
        if (user == null){
            user = "";
        }

        if (mealTime == null){
            mealTime = "";
        }



        String mealtime_str = mealTime;
        if(user.equals("사용자1"))
            user = "1";
        else if(user.equals("사용자2")){
            user = "2";
        }

        if(mealTime.equals("아침"))
            mealTime = "1";
        else if(mealTime.equals("점심")){
            mealTime = "2";
        }else if(mealTime.equals("저녘")){
            mealTime = "3";
        }

        TextView textView = findViewById(R.id.txt_title);
        textView.setText("사용자 :" +user+"\n" + mealTime + ",");

        Button btn_auth = findViewById(R.id.btn_authentication);
        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("value");
                AlarmEnable alarm =new AlarmEnable("1", mealTime, user);
                myRef.setValue(alarm);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }




}