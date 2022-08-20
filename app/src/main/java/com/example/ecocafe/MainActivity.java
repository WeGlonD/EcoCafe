package com.example.ecocafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView main_bottom = findViewById(R.id.main_bottom);
        BottomNavigationHelper.disableShiftMode(main_bottom);
/*
        homeFragment = new HomeFragment();
        listFragment = new ListFragment();
        myPageFragment = new MyPageFragment();
        mapFragment = new MapFragment();
 */

    }
/*
    public void setFragment(int n){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (n){
            case 0:
                fragmentTransaction.replace(R.id.main_frame,homeFragment).commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.main_frame, hotCommunityFragment).commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.main_frame,myPageFragment).commit();
                break;
            case 3:
                fragmentTransaction.replace(R.id.main_frame,alarmFragment).commit();
                break;
        }
*/
}