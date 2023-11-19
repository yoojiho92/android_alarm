package com.example.remotebt.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.remotebt.R;
import com.example.remotebt.model.AddUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddFragment extends Fragment {
    private AlertDialog loadingDialog;
    private ProgressBar progressBar_loading;
    private TextView tv_loading_text;


    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ladd, container, false);

        // 버튼 바인드
        Button btn_add_1 = view.findViewById(R.id.btn_add_user_1);
        Button btn_add_2 = view.findViewById(R.id.btn_add_user_2);

        btn_add_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference myRef = database.getReference("user_add");
                AddUser addUser =new AddUser("1", "0", "0");
                myRef.setValue(addUser);
                showLoadingDialog();

                DatabaseReference completeRef = myRef.child("complete");
                completeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // 데이터가 변경되면 이 메소드가 호출됩니다.
                        String complete = snapshot.getValue(String.class);
                        Log.e("LOG_COMPLETE",complete);
                        if(complete.equals("1")){
                            hideLoadingDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hideLoadingDialog();
                    }
                });
            }
        });

        btn_add_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef = database.getReference("user_add");
                AddUser addUser =new AddUser("2", "0", "0");
                myRef.setValue(addUser);

                showLoadingDialog();

                DatabaseReference completeRef = myRef.child("complete");
                completeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // 데이터가 변경되면 이 메소드가 호출됩니다.
                        String complete = snapshot.getValue(String.class);
                        Log.e("LOG_COMPLETE",complete);
                        if(complete.equals("1")){
                            hideLoadingDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hideLoadingDialog();
                    }
                });
            }
        });


        return view;
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.loading_dialog, null);
            progressBar_loading = dialogView.findViewById(R.id.progressBar);
            tv_loading_text = dialogView.findViewById(R.id.tv_loading_text);

            builder.setView(dialogView);
            builder.setCancelable(false); // 클릭을 막기 위해 false로 설정

            loadingDialog = builder.create();
        }

        DatabaseReference myRef = database.getReference("user_add");
        DatabaseReference completeRef = myRef.child("now_pic");
        completeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터가 변경되면 이 메소드가 호출됩니다.
                
                // now_pic값을 읽어와
                // Float형으로 변환시키고
                // TextView에 출력
                String pic_num = snapshot.getValue(String.class);
                Log.d("PIC_num", pic_num);
                progressBar_loading.setProgress(Integer.parseInt(pic_num));
                float persent = (Float.valueOf(pic_num) / 101.0f *100.0f);
                persent = (float) (Math.floor(persent * 10) / 10);
                tv_loading_text.setText(String.valueOf(persent) + "%");
                if(pic_num.equals("101")){
                    tv_loading_text.setText("학습중");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoadingDialog();
            }
        });
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
