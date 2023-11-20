package com.example.remotebt.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotebt.OnItemClickListener;
import com.example.remotebt.model.AlarmDAO;
import com.example.remotebt.R;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    // 알람 리스트
    private ArrayList<AlarmDAO> dataList;
    private OnItemClickListener onItemClickListener;

    // 알람 생성자
    public AlarmAdapter(ArrayList<AlarmDAO> dataList) {
        this.dataList = dataList;
    }


    // 뷰홀더 생성
    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item_layout, parent, false);
        return new AlarmViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // 뷰 바인드
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AlarmDAO alarmDAO= dataList.get(position);
        // 시간 분 부분 한자리수일경우,
        // 앞에 0 을 추가하여 2자리 수로 변경
        String get_time = alarmDAO.getTime().toString();
        String[] split =get_time.split(":");
        if (split[1].length() < 2){
            split[1] = "0" + split[1] ;
        }
        get_time = split[0] + ":" + split[1];
        
        // 각 값들 TextView에 출력
        holder.textView.setText(get_time);
        holder.tv_meal.setText(alarmDAO.getMealtime());
        holder.tv_user.setText(alarmDAO.getUser());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    // 알람 리스트의 수
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // 뷰홀더 선언
    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView tv_meal;
        TextView tv_user;

        // 뷰홀더 바인드
        public AlarmViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItem);
            tv_meal = itemView.findViewById(R.id.tv_alarm_item_meal);
            tv_user = itemView.findViewById(R.id.tv_alarm_item_user);
        }
    }

    public void removeItem(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }
    public ArrayList<AlarmDAO> getArray(){
        return dataList;
    }
}