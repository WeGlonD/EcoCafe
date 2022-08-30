package com.example.ecocafe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Cafe_List extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Cafe_item> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();//객체 담을 어레이리스트


        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("cafe");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear();//기존 배열리스트가 존재하지 않게 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){//반복문으로 데이터 List를 추출해냄
                    Cafe_item cafe_item = snapshot.getValue(Cafe_item.class);
                    arrayList.add(cafe_item);//담은 데이터들을 배열리스트에 넣고 리사이클러 뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged();//리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비를 가져오던 중 에러 발생 시
                //Log.e("Cafe_List",String.valueOf(databaseError.toException()));
            }
        });

        //adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);
    }
}
