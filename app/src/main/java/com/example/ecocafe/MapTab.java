package com.example.ecocafe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ecocafe.firebase.Acts;
import com.example.ecocafe.firebase.Cafe;
import com.example.ecocafe.firebase.CafeQuery;
import com.example.ecocafe.firebase.Database;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapTab extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    View view;
    MapView mapView;
    GoogleMap googleMap;
    Marker currentMarker = null;
    FrameLayout mLayout;
    boolean first = true;

    private ArrayList<Cafe_item> cafes;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    private static final String TAG = "googlemap_ecocafe";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

    Location mCurrentLocation;
    LatLng currentPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maptab, container, false);
        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mLayout = view.findViewById(R.id.main_frame);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        /*
        //동기 db활용
        cafes = new ArrayList<>();//객체 담을 어레이리스트
        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("cafe");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                cafes.clear();//기존 배열리스트가 존재하지 않게 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {//반복문으로 데이터 List를 추출해냄
                    Cafe_item cafe_item = snapshot.getValue(Cafe_item.class);
                    cafes.add(cafe_item);//담은 데이터들을 배열리스트에 넣고 리사이클러 뷰로 보낼 준비
                }
                googleMap.clear();
                for(Cafe_item cafe : cafes){
                    LatLng cafePosition = new LatLng(cafe.getLat(),cafe.getLng());
                    MarkerOptions markerOptions = new MarkerOptions().title(cafe.getName()).position(cafePosition).snippet(getCurrentAddress(cafePosition));
                    googleMap.addMarker(markerOptions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비를 가져오던 중 에러 발생 시
                //Log.e("Cafe_List",String.valueOf(databaseError.toException()));
            }
        });
        */

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        this.googleMap = googleMap;
        setDefaultLocation();

        //밑에 두 줄 대신에 firebase에서 정보 갖고와서 마커 찍는 코드 작성해야함.
        //MarkerOptions options = new MarkerOptions().position(new LatLng(35.889131,128.591447)).title("KFC 침산네거리점").snippet(getCurrentAddress(new LatLng(35.889131,128.591447)));
        //Marker tmp = googleMap.addMarker(options);


        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                double distance = SphericalUtil.computeDistanceBetween(currentPosition, marker.getPosition());
                Toast.makeText(getContext(), "현위치부터 " + String.format("%.2f", distance/1000) + "km 거리", Toast.LENGTH_LONG).show();
            }
        });


        //민석이 db활용
        ArrayList<Cafe> cafes = new ArrayList<>();
        Database db = new Database();
        db.readAllCafe(cafes, new CafeQuery() {
            @Override
            public boolean Q(Cafe cafe) {
                return true;
            }
        }, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                for(Cafe tmp : cafes){
                     MarkerOptions markerOptions = new MarkerOptions()
                             .title(tmp.getName())
                             .position(new LatLng(tmp.getLat(),tmp.getLng()))
                             .snippet(getCurrentAddress(new LatLng(tmp.getLat(),tmp.getLng())));
                     googleMap.addMarker(markerOptions);
                }
            }

            @Override
            public void ifFail(Object task) {
                return;
            }
        });


        //위치 퍼미션 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }else{
            //퍼미션 거부한 적 있는 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),REQUIRED_PERMISSIONS[0])){
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }else{
                //퍼미션 거부 한적 없으면 퍼미션 요청 바로 함
                //요청결과는 onRequestPermissionResult에 수신
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick :");
            }
        });

    }

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
            if(locationList.size()>0){
                location = locationList.get(locationList.size() - 1);
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) + "경도:" + String.valueOf(location.getLongitude());
                Log.d(TAG, "onLocationResult : " + markerSnippet);
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocation = location;
            }
        }
    };

    private void startLocationUpdates(){
        if(!checkLocationServicesStatus()){
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else{
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

            if(hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
            hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "권한없음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

            if(checkPermission()){
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart");
        if(checkPermission()){
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);
            if(googleMap != null){
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    public void onStop(){
        super.onStop();
        if(mFusedLocationClient != null){
            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public String getCurrentAddress(LatLng latlng){
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;

        try{
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        }catch(IOException ioException){
            //네트워크 문제
            Toast.makeText(getContext(),"지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        }catch(IllegalArgumentException illegalArgumentException){
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if(addresses == null || addresses.size() == 0){
            //Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }else{
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet){
        if(currentMarker != null) currentMarker.remove();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        //currentMarker = googleMap.addMarker(markerOptions);

        if(first) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            googleMap.moveCamera(cameraUpdate);
            first = false;
        }
    }

    public void setDefaultLocation() {
        //디폴트 위치 서울
        LatLng DEFAULT_LOCATION = new LatLng(37.56,126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부를 확인하세요";

        if(currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //currentMarker = googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION,15);
        googleMap.moveCamera(cameraUpdate);
    }

    private boolean checkPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    //ActivityCompat.requestPermissions 퍼미션 요청의 결과를 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length){
            boolean check_result = true;

            //모든 퍼미션 허용했는지 체크
            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if(check_result){
                //퍼미션 허용했으니 위치업데이트 시작
                startLocationUpdates();
            }
            else{
                //퍼미션 거부된거 있으면 종료
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),REQUIRED_PERMISSIONS[0])||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),REQUIRED_PERMISSIONS[1])){
                    //유저가 거부만 선택한 경우 앱 재실행시 퍼미션 허용 가능
                    Snackbar.make(mLayout, "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                        }
                    }).show();
                }else{
                    //다시묻지않음 체크시 설정에서 퍼미션 허용해야함
                    Snackbar.make(mLayout, "권한이 거부되었습니다. 설정(앱 정보)에서 권한을 허용해야 합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                        }
                    }).show();
                }
            }
        }
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
                //gps활성 체크
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.d(TAG, "onActivityResult : GPS 활성화 돼있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
