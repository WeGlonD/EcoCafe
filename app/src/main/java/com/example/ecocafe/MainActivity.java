package com.example.ecocafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.*;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    MapTab mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView main_bottom = findViewById(R.id.main_bottom);
        BottomNavigationHelper.disableShiftMode(main_bottom);

        //homeFragment = new HomeFragment();
        //listFragment = new ListFragment();
        //myPageFragment = new MyPageFragment();
        mapFragment = new MapTab();

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setFragment(item.getItemId());
                return true;
            }
        });

    }

    public void setFragment(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (n) {
            case 0:
                //fragmentTransaction.replace(R.id.main_frame,homeFragment).commit();
                break;
            case R.id.bottom_map:
                fragmentTransaction.replace(R.id.main_frame, mapFragment).commit();
                break;
            case 2:
                //fragmentTransaction.replace(R.id.main_frame,myPageFragment).commit();
                break;
            case 3:
                //fragmentTransaction.replace(R.id.main_frame,alarmFragment).commit();
                break;
        }
    }
}