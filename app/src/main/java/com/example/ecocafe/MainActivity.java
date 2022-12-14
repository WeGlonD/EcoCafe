package com.example.ecocafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecocafe.firebase.Acts;
import com.example.ecocafe.firebase.Database;
import com.example.ecocafe.firebase.Reacts;
import com.example.ecocafe.firebase.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    MapTab mapFragment;

    MainActivity mainInstance;
    ConstraintLayout mainActivityLayout;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    boolean needRequest = false;

    private static final String TAG = "Main_ecocafe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainInstance = this;
        mainActivityLayout = findViewById(R.id.mainActivityLayout);

        BottomNavigationView main_bottom = findViewById(R.id.main_bottom);
        BottomNavigationHelper.disableShiftMode(main_bottom);

        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, PhotoActivity.class);
                startActivity(it);
            }
        });

        //?????? ????????? ??????,????????? ??????,????????? ?????? ?????? ??????
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int CameraPermissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        int hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                CameraPermissionCheck == PackageManager.PERMISSION_GRANTED && hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED &&
                hasReadExternalStoragePermission == PackageManager.PERMISSION_GRANTED){
            //startLocationUpdates();
        }else{
            //????????? ????????? ??? ?????? ??????
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])){
                Snackbar.make(mainActivityLayout, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(mainInstance, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }
            else if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[2])){
                Snackbar.make(mainActivityLayout, "??? ?????? ??????????????? ????????? ?????? ????????? ???????????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(mainInstance, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }
            else if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[3])){
                Snackbar.make(mainActivityLayout, "??? ?????? ??????????????? ????????? ?????? ????????? ???????????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(mainInstance, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }
            else{
                //????????? ?????? ?????? ????????? ????????? ?????? ?????? ???
                //??????????????? onRequestPermissionResult??? ??????
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }



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

//        //?????? ????????? ??? ?????? ?????? ??????(??????)
//        EditText email = findViewById(R.id.email);
//        EditText password = findViewById(R.id.password);
//        Button create_btn = findViewById(R.id.create_btn);
//        Button login_btn = findViewById(R.id.login_btn);
//        Button logout_btn = findViewById(R.id.logout_btn);
//        Button delete_btn = findViewById(R.id.delete_btn);
//        TextView user_name = findViewById(R.id.name);
//        TextView user_point = findViewById(R.id.point);
//
//        User user = new User(getApplicationContext());
//
//        if (mDB.getAuth().getCurrentUser() != null){
//            mDB.getAuth().signOut();
//        }
//
//        user_name.setText("-----");
//        user_point.setText(String.valueOf(0));
//
//        create_btn.setOnClickListener(view -> {
//            Boolean isManager = true;
//            String name = "dirtfy";
//            Long point = Long.valueOf(1000);
//            User.Data data = new User.Data(isManager, name, point);
//            user.create(email.getText().toString(), password.getText().toString(), new Acts() {
//                @Override
//                public void ifSuccess(Object task) {
//                    mDB.writeUser(data);
//                    user_name.setText(name);
//                    user_point.setText(String.valueOf(point));
//                    user.login(email.getText().toString(), password.getText().toString(), new Acts() {
//                        @Override
//                        public void ifSuccess(Object task) {
//                            afterLoginSuccess(mDB, user_name, user_point);
//                        }
//
//                        @Override
//                        public void ifFail(Object task) {
//
//                        }
//                    });
//                }
//
//                @Override
//                public void ifFail(Object task) {
//
//                }
//            });
//        });
//        login_btn.setOnClickListener(view -> {
//            user.login(email.getText().toString(), password.getText().toString(), new Acts() {
//                @Override
//                public void ifSuccess(Object task) {
//                    afterLoginSuccess(mDB, user_name, user_point);
//                }
//
//                @Override
//                public void ifFail(Object task) {
//
//                }
//            });
//        });
//        logout_btn.setOnClickListener(view -> {
//            user.logout();
//            user_name.setText("-----");
//            user_point.setText(String.valueOf(0));
//        });
//        delete_btn.setOnClickListener(view -> {
//            user.delete(new Acts() {
//                @Override
//                public void ifSuccess(Object task) {
//                    user_name.setText("-----");
//                    user_point.setText(String.valueOf(0));
//                }
//
//                @Override
//                public void ifFail(Object task) {
//
//                }
//            });
//        });
//
//        Button write_btn = findViewById(R.id.write_btn);
//        Button read_btn = findViewById(R.id.read_btn);
//        write_btn.setOnClickListener(view -> {
//            mDB.readUser(new Acts() {
//                @Override
//                public void ifSuccess(Object task) {
//                    User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
//                    data.setPoint(data.getPoint()+1);
//                    mDB.writeUser(data);
//                }
//
//                @Override
//                public void ifFail(Object task) {
//
//                }
//            });
//        });
//        read_btn.setOnClickListener(view -> {
//            mDB.readUser(new Acts() {
//                @Override
//                public void ifSuccess(Object task) {
//                    User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
//                    user_name.setText(data.getName());
//                    user_point.setText(String.valueOf(data.getPoint()));
//                }
//
//                @Override
//                public void ifFail(Object task) {
//
//                }
//            });
//        });
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

//    public void afterLoginSuccess(Database mDB, TextView user_name, TextView user_point){
//        mDB.readUser(new Acts() {
//            @Override
//            public void ifSuccess(Object task) {
//                User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
//                user_name.setText(data.getName());
//                user_point.setText(String.valueOf(data.getPoint()));
//                mDB.setUserValueEventListener(new Reacts() {
//                    @Override
//                    public void ifDataChanged(DataSnapshot dataSnapshot) {
//                        User.Data data = dataSnapshot.getValue(User.Data.class);
//                        if(data != null){
//                            user_name.setText(data.getName());
//                            user_point.setText(String.valueOf(data.getPoint()));
//                        }
//                    }
//
//                    @Override
//                    public void ifCancelled(DatabaseError error) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void ifFail(Object task) {
//
//            }
//        });
//    }

    private boolean checkPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    //ActivityCompat.requestPermissions ????????? ????????? ????????? ???????????? ?????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length){
            boolean check_result = true;

            //?????? ????????? ??????????????? ??????
            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if(check_result){
                //????????? ??????????????? ?????????????????? ??????
                //startLocationUpdates();
            }
            else{
                //????????? ???????????? ????????? ??????
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[1])||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[2])||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[3])||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[4])){
                    //????????? ????????? ????????? ?????? ??? ???????????? ????????? ?????? ??????
                    Snackbar.make(mainActivityLayout, "????????? ?????????????????????. ?????? ?????? ???????????? ????????? ??????????????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }else{
                    //?????????????????? ????????? ???????????? ????????? ???????????????
                    Snackbar.make(mainActivityLayout, "????????? ?????????????????????. ??????(??? ??????)?????? ????????? ???????????? ?????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n" + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GPS_ENABLE_REQUEST_CODE:
                //gps?????? ??????
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.d(TAG, "onActivityResult : GPS ????????? ?????????");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }
}