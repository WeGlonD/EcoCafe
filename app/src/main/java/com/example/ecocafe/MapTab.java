package com.example.ecocafe;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.PanelSlideListener;
import com.sothree.slidinguppanel.PanelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MapTab extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    View view;
    MapView mapView;
    GoogleMap googleMap;
    Marker currentMarker = null;
    FrameLayout mLayout;
    boolean first = true;

    private ArrayList<Cafe> cafes;
    private ArrayList<Cafe> fixedCafes;

    private SlidingUpPanelLayout sliding;

    private RecyclerView cafeListRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final String TAG = "googlemap_ecocafe";
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    Location mCurrentLocation;
    public static LatLng currentPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maptab, container, false);
        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mLayout = view.findViewById(R.id.main_frame);


        sliding = view.findViewById(R.id.slide);
        sliding.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                Collections.sort(cafes);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPanelStateChanged(View view, PanelState panelState, PanelState panelState1) {
                return;
            }
        });

        cafeListRecycler = view.findViewById(R.id.cafeListRecycler);
        cafeListRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        cafeListRecycler.setLayoutManager(layoutManager);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        this.googleMap = googleMap;
        setDefaultLocation();

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //double distance = SphericalUtil.computeDistanceBetween(currentPosition, marker.getPosition());
                //Toast.makeText(getContext(), "현위치부터 " + String.format("%.2f", distance/1000) + "km 거리", Toast.LENGTH_LONG).show();
                int idx = (int)marker.getTag();
                Cafe first = fixedCafes.get(idx);
                CustomAdapter.CustomViewHolder holder = new CustomAdapter.CustomViewHolder(view.findViewById(R.id.cafe_item_layout));
                Glide.with(holder.itemView)
                        .load(first.getPic())
                        .into(holder.iv_pic);

                holder.tv_event.setText(first.getEvent());
                holder.tv_name.setText(first.getName());
                holder.tv_link.setText(first.getLink());
            }
        });


        //민석이 db활용
        cafes = new ArrayList<>();
        fixedCafes = new ArrayList<>();
        Database db = new Database();
        db.readAllCafe(cafes, new CafeQuery() {
            @Override
            public boolean Q(Cafe cafe) {
                return true;
            }
        }, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                Collections.sort(cafes);
                for(int i = 0; i < cafes.size(); i++){
                     Cafe tmp = cafes.get(i);
                     fixedCafes.add(tmp);
                     MarkerOptions markerOptions = new MarkerOptions()
                             .title(tmp.getName())
                             .position(new LatLng(tmp.getLat(),tmp.getLng()))
                             .snippet(getCurrentAddress(new LatLng(tmp.getLat(),tmp.getLng())));
                     Marker marker = googleMap.addMarker(markerOptions);
                     marker.setTag(i);
                }
                Toast.makeText(getContext(), ""+cafes.size(), Toast.LENGTH_LONG).show();
                //슬라이딩업패널에 리스트 구축(현위치 거리 정렬)
                adapter = new CustomAdapter(cafes, getContext());
                cafeListRecycler.setAdapter(adapter);
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
        }
        else{
                Toast.makeText(getContext(),"권한이없습니다!", Toast.LENGTH_LONG).show();
                getActivity().finish();
        }


        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

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
            //Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            //showDialogForLocationServiceSetting();
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
        currentPosition = DEFAULT_LOCATION;
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
