package com.example.remotebt.callback;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotebt.adapter.AlarmAdapter;
import com.example.remotebt.database.DatabaseHelper;
import com.example.remotebt.model.AlarmDAO;
import com.example.remotebt.reciver.NotificationReceiver;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private AlarmAdapter adapter;
    private DatabaseHelper databaseHelper;
    private Context context;

    public SwipeToDeleteCallback(Context context, AlarmAdapter adapter, DatabaseHelper databaseHelper) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.databaseHelper = databaseHelper;
        this.context = context;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false; // 드래그 & 드롭을 사용하지 않으므로 false 반환
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        AlarmDAO alarmDAO = adapter.getArray().get(position);


        int alarmNo = databaseHelper.getAlarmNo(alarmDAO.getTime(), alarmDAO.getUser(), alarmDAO.getMealtime());

        if (alarmNo != -1) {
            Intent receiverIntent = new Intent(context, NotificationReceiver.class);
            receiverIntent.putExtra("content", "알람리스트 테스트");
            receiverIntent.putExtra("mealtime", alarmDAO.getMealtime());
            receiverIntent.putExtra("user", alarmDAO.getUser());
            receiverIntent.putExtra("alarm_no", alarmNo);

            Log.d("AlarmSwipeDelete", "Alarm No: " + alarmNo +
                    ", Time: " + alarmDAO.getTime() +
                    ", User: " + alarmDAO.getUser() +
                    ", Mealtime: " + alarmDAO.getMealtime());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmNo,
                    receiverIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );


            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            databaseHelper.deleteAlarm(alarmNo);
        }

        adapter.removeItem(position);
    }
}
