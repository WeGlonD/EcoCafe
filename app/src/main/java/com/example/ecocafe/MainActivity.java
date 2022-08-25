package com.example.ecocafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ecocafe.firebase.Acts;
import com.example.ecocafe.firebase.ActsTest;
import com.example.ecocafe.firebase.Database;
import com.example.ecocafe.firebase.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;

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

        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, Cafe_List.class);
                startActivity(it);
            }
        });

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


        ///유저 로그인 및 유저 정보 저장(예시)
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button create_btn = findViewById(R.id.create_btn);
        Button login_btn = findViewById(R.id.login_btn);
        Button logout_btn = findViewById(R.id.logout_btn);
        Button delete_btn = findViewById(R.id.delete_btn);
        User user = new User(getApplicationContext());
        Database mDB = new Database(getApplicationContext());
        create_btn.setOnClickListener(view -> {
            user.create(email.getText().toString(), password.getText().toString(), new ActsTest());
            String name = "dirtfy";
            Long point = Long.valueOf(1000);
            user.data = new User.Data(name, point);
            mDB.writeUser(user.data);
        });
        login_btn.setOnClickListener(view -> {
            user.login(email.getText().toString(), password.getText().toString(), new ActsTest());
            mDB.readUser();
        });
        logout_btn.setOnClickListener(view -> {
            user.logout(new ActsTest());
        });
        delete_btn.setOnClickListener(view -> {
            user.delete(new ActsTest());
        });

        Button write_btn = findViewById(R.id.write_btn);
        Button read_btn = findViewById(R.id.read_btn);
        write_btn.setOnClickListener(view -> {
            if(user.data != null){
                user.data.setPoint(user.data.getPoint()+1);
                mDB.writeUser(user.data);
            }
        });
        read_btn.setOnClickListener(view -> {
            mDB.readUser();
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