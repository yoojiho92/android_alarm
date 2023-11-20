package com.example.remotebt.fragment;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotebt.AlarmActivity;
import com.example.remotebt.OnItemClickListener;
import com.example.remotebt.R;
import com.example.remotebt.callback.SwipeToDeleteCallback;
import com.example.remotebt.adapter.AlarmAdapter;
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

public class ListFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();

    int currHour, currMinute;

    private AlarmManager alarmManager;

    private int d_hour = 0;
    private int d_minute = 0;

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private ArrayList<AlarmDAO> datalist;
    private DatabaseHelper dbHelper;
    
    // mode false : 1인 모드
    //       true : 2인 모드
    private boolean mode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        dbHelper = new DatabaseHelper(getContext());
        
        
        Switch aSwitch = view.findViewById(R.id.sw_fragment_list_mode);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Util.save_pref(getContext(),"mode", "true");
                    mode = true;
                }else{
                    Util.save_pref(getContext(),"mode", "false");
                    mode = false;
                }
                
            }
        });


        if(Util.get_pref(getContext(),"mode").equals("true")){
            mode = true;
            aSwitch.setChecked(true);
        }
        
        
        
        Button btnAddAlarm = view.findViewById(R.id.btn_addAlarm);
        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode){
                    showAddDialog();    
                }else{
                    showAddDialog_user_1();
                }
                
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime now = LocalTime.now();
            currHour = now.getHour();
            currMinute = now.getMinute();
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        datalist = new ArrayList<AlarmDAO>();

        // 더 많은 아이템 추가...

        alarmAdapter = new AlarmAdapter(datalist);
        alarmAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AlarmDAO alarmDAO = datalist.get(position);
                Intent intent2 = new Intent(getContext(), AlarmActivity.class);
                intent2.putExtra("content", "알람리스트 테스트");
                intent2.putExtra("mealtime", alarmDAO.getMealtime());
                intent2.putExtra("user", alarmDAO.getUser());
                intent2.putExtra("alarm_no", alarmDAO.getAlarm_no());
                startActivity(intent2);
            }
        });
        recyclerView.setAdapter(alarmAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext(),alarmAdapter,dbHelper));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        MediaPlayerManager.stopAlarm();
        load_db();

        return view;
    }


    private void load_db(){
        List<AlarmDAO> alarms = dbHelper.getAllAlarms();
        datalist.clear();
        for (AlarmDAO alarm : alarms) {
            AlarmDAO newAlarm = new AlarmDAO(alarm.getAlarm_no(),alarm.getTime(), alarm.getUser(), alarm.getMealtime());
            datalist.add(newAlarm);
            // 출력 또는 다른 처리
            Log.d("AlarmInfo", "Alarm No: " + alarm.getAlarm_no() +
                    ", Time: " + alarm.getTime() +
                    ", User: " + alarm.getUser() +
                    ", Mealtime: " + alarm.getMealtime());
        }
        alarmAdapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        final Dialog dialog = new Dialog(getContext());
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
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String selectedMealTime = ((RadioButton)dialog.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                String selectedUser = ((RadioButton)dialog.findViewById(rg_user.getCheckedRadioButtonId())).getText().toString();


                AlarmDAO newAlarm = new AlarmDAO(hour+":"+minute, selectedUser, selectedMealTime);
                int alarmNo = dbHelper.insertOrUpdateAlarm(newAlarm);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setNotice(hour + ":" + minute + ":" + "00", selectedMealTime, selectedUser, alarmNo);
                }
                load_db();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showAddDialog_user_1() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.add_single_dialog_layout);
        dialog.setTitle("알람 추가");

        TimePicker timePicker = dialog.findViewById(R.id.timePicker);
        RadioGroup rg_position = dialog.findViewById(R.id.radioGroup);

        Button dialogButton = dialog.findViewById(R.id.dialogButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String selectedUser = "사용자1";
                String selectedMealTime = ((RadioButton)dialog.findViewById(rg_position.getCheckedRadioButtonId())).getText().toString();


                AlarmDAO newAlarm = new AlarmDAO(hour+":"+minute, selectedUser, selectedMealTime);
                int alarmNo = dbHelper.insertOrUpdateAlarm(newAlarm);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setNotice(hour + ":" + minute + ":" + "00", selectedMealTime, selectedUser, alarmNo);
                }
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
    private void setNotice(String alarmTimeValue, String mealtime, String user, int alarmNo) {
        Intent receiverIntent = new Intent(getContext(), NotificationReceiver.class);
        receiverIntent.putExtra("content", "알람리스트 테스트");
        receiverIntent.putExtra("mealtime", mealtime);
        receiverIntent.putExtra("user", user);
        receiverIntent.putExtra("alarm_no", alarmNo);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmNo, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
