package com.example.remotebt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.remotebt.fragment.AddFragment;
import com.example.remotebt.fragment.ListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NavigationMenu extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private Fragment listFragment = new ListFragment();
    private Fragment AddFragment = new AddFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);
        bottomNavigationView = findViewById(R.id.navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, listFragment).commitAllowingStateLoss();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        getSupportFragmentManager().beginTransaction() .replace(R.id.frame_container,listFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.navigation_add:
                        getSupportFragmentManager().beginTransaction() .replace(R.id.frame_container,AddFragment).commitAllowingStateLoss();
                        // 대시보드 프래그먼트 로직
                        return true;
                }
                return false;
            }
        });
    }
}