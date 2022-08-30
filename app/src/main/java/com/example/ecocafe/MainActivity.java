package com.example.ecocafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ecocafe.firebase.Acts;
import com.example.ecocafe.firebase.Database;
import com.example.ecocafe.firebase.Reacts;
import com.example.ecocafe.firebase.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

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
        Database mDB = new Database(getApplicationContext());
/*
        ///유저 로그인 및 유저 정보 저장(예시)
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button create_btn = findViewById(R.id.create_btn);
        Button login_btn = findViewById(R.id.login_btn);
        Button logout_btn = findViewById(R.id.logout_btn);
        Button delete_btn = findViewById(R.id.delete_btn);
        TextView user_name = findViewById(R.id.name);
        TextView user_point = findViewById(R.id.point);

        User user = new User(getApplicationContext());

        if (mDB.getAuth().getCurrentUser() != null){
            mDB.getAuth().signOut();
        }

        user_name.setText("-----");
        user_point.setText(String.valueOf(0));

        create_btn.setOnClickListener(view -> {
            Boolean isManager = true;
            String name = "dirtfy";
            Long point = Long.valueOf(1000);
            User.Data data = new User.Data(isManager, name, point);
            user.create(email.getText().toString(), password.getText().toString(), new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    mDB.writeUser(data);
                    user_name.setText(name);
                    user_point.setText(String.valueOf(point));
                    user.login(email.getText().toString(), password.getText().toString(), new Acts() {
                        @Override
                        public void ifSuccess(Object task) {
                            afterLoginSuccess(mDB, user_name, user_point);
                        }

                        @Override
                        public void ifFail(Object task) {

                        }
                    });
                }

                @Override
                public void ifFail(Object task) {

                }
            });
        });
        login_btn.setOnClickListener(view -> {
            user.login(email.getText().toString(), password.getText().toString(), new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    afterLoginSuccess(mDB, user_name, user_point);
                }

                @Override
                public void ifFail(Object task) {

                }
            });
        });
        logout_btn.setOnClickListener(view -> {
            user.logout();
            user_name.setText("-----");
            user_point.setText(String.valueOf(0));
        });
        delete_btn.setOnClickListener(view -> {
            user.delete(new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    user_name.setText("-----");
                    user_point.setText(String.valueOf(0));
                }

                @Override
                public void ifFail(Object task) {

                }
            });
        });

        Button write_btn = findViewById(R.id.write_btn);
        Button read_btn = findViewById(R.id.read_btn);
        write_btn.setOnClickListener(view -> {
            mDB.readUser(new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
                    data.setPoint(data.getPoint()+1);
                    mDB.writeUser(data);
                }

                @Override
                public void ifFail(Object task) {

                }
            });
        });
        read_btn.setOnClickListener(view -> {
            mDB.readUser(new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
                    user_name.setText(data.getName());
                    user_point.setText(String.valueOf(data.getPoint()));
                }

                @Override
                public void ifFail(Object task) {

                }
            });
        });*/
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

    public void afterLoginSuccess(Database mDB, TextView user_name, TextView user_point){
        mDB.readUser(new Acts() {
            @Override
            public void ifSuccess(Object task) {
                User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
                user_name.setText(data.getName());
                user_point.setText(String.valueOf(data.getPoint()));
                mDB.setUserValueEventListener(new Reacts() {
                    @Override
                    public void ifDataChanged(DataSnapshot dataSnapshot) {
                        User.Data data = dataSnapshot.getValue(User.Data.class);
                        if(data != null){
                            user_name.setText(data.getName());
                            user_point.setText(String.valueOf(data.getPoint()));
                        }
                    }

                    @Override
                    public void ifCancelled(DatabaseError error) {

                    }
                });
            }

            @Override
            public void ifFail(Object task) {

            }
        });
    }
}