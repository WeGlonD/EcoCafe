package com.example.ecocafe.firebase;

import com.google.android.gms.maps.model.LatLng;
//데이터 베이스(쿼리 조건 넘겨줌)
public interface CafeQuery {
    boolean Q(Cafe cafe);
}
