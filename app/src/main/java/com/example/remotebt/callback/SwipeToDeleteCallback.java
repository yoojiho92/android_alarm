package com.example.remotebt.callback;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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

        Intent receiverIntent = new Intent(context, NotificationReceiver.class);
        receiverIntent.putExtra("content", "알람리스트 테스트");
        receiverIntent.putExtra("mealtime", alarmDAO.getMealtime());
        receiverIntent.putExtra("user", alarmDAO.getUser());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                123, // 알람 설정 시 사용했던 동일한 요청 코드
                receiverIntent, // 알람 설정 시 사용했던 동일한 인텐트
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // AlarmManager 인스턴스를 가져옵니다.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 알람을 해제합니다.
        alarmManager.cancel(pendingIntent);

        // DB에서 해당 알람을 삭제합니다.
        databaseHelper.deleteAlarm(alarmDAO.getTime());
        // 리스트 뷰에서 알람 요소를 삭제합니다.
        adapter.removeItem(position);


    }
}
