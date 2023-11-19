package com.example.remotebt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.remotebt.adapter.AlarmAdapter;
import com.example.remotebt.callback.SwipeToDeleteCallback;
import com.example.remotebt.database.DatabaseHelper;
import com.example.remotebt.model.AlarmDAO;
import com.example.remotebt.reciver.NotificationReceiver;
import com.example.remotebt.util.MediaPlayerManager;
import com.example.remotebt.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {
    // 필요한 멤버 변수 선언
    // 예: ListView listView, ArrayAdapter<String> adapter 등
    private String TAG = this.getClass().getSimpleName();

    int currHour, currMinute;

    private AlarmManager alarmManager;

    private int d_hour = 0;
    private int d_minute = 0;

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private ArrayList<AlarmDAO> datalist;
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new DatabaseHelper(this);




        Button btnAddAlarm = findViewById(R.id.btn_addAlarm);
        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime now = LocalTime.now();
            currHour = now.getHour();
            currMinute = now.getMinute();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        datalist = new ArrayList<AlarmDAO>();

        // 더 많은 아이템 추가...

        alarmAdapter = new AlarmAdapter(datalist);
        recyclerView.setAdapter(alarmAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getApplicationContext(),alarmAdapter,dbHelper));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        MediaPlayerManager.stopAlarm();
        load_db();
    }

    private void load_db(){
        List<AlarmDAO> alarms = dbHelper.getAllAlarms();
        datalist.clear();
        for (AlarmDAO alarm : alarms) {
            AlarmDAO newAlarm = new AlarmDAO(alarm.getTime(), alarm.getUser(), alarm.getMealtime());
            datalist.add(newAlarm);
            alarmAdapter.notifyDataSetChanged();
            // 출력 또는 다른 처리
            Log.d("AlarmInfo", "Time: " + alarm.getTime() + ", User: " + alarm.getUser() + ", Mealtime: " + alarm.getMealtime());
        }
    }

    private void showAddDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_dialog_layout);
        dialog.setTitle("알람 추가");

        TimePicker timePicker = dialog.findViewById(R.id.timePicker);
        TextView selectedTime = dialog.findViewById(R.id.selectedTime);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        RadioGroup rg_user = dialog.findViewById(R.id.radioGroup_user);
        
        Button dialogButton = dialog.findViewById(R.id.dialogButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    if (rg_user.getCheckedRadioButtonId() != -1) {
                        Toast.makeText(ListActivity.this, "유저를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ListActivity.this, "아침/점심/저녘 을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String selectedMealTime = ((RadioButton)dialog.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                String selectedUser = ((RadioButton)dialog.findViewById(rg_user.getCheckedRadioButtonId())).getText().toString();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setNotice(hour + ":" + minute + ":" + "00", selectedMealTime, selectedUser);
                }
                AlarmDAO newAlarm = new AlarmDAO(hour+":"+minute, selectedUser, selectedMealTime);
                dbHelper.insertOrUpdateAlarm(newAlarm);
                load_db();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public static String formatTime(String timeValue) {
        DateFormat reqDateFormat = new SimpleDateFormat("HH:mm");
        DateFormat resDateFormat = new SimpleDateFormat("a hh:mm", Locale.KOREAN);
        Date datetime = null;

        try {

            datetime = reqDateFormat.parse(timeValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resDateFormat.format(datetime);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotice(String alarmTimeValue, String mealtime, String user) {
        Intent receiverIntent = new Intent(this, NotificationReceiver.class);
        receiverIntent.putExtra("content", "알람리스트 테스트");
        receiverIntent.putExtra("mealtime", mealtime);
        receiverIntent.putExtra("user", user);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDate now = LocalDate.now();
        Date datetime = null;

        try {
            datetime = dateFormat.parse(now + " " + alarmTimeValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datetime);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

    }
}