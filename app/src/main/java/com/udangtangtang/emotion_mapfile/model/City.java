package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenterCallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class City{

    private final String TAG = "City";
    private final FirebaseDatabase firebaseDatabase;
    private double latitude;
    private double longitude;
    private String myCity;
    private long temperature;

    public City() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    // HashMap을 기반으로 comment 속성만으로 이루어진 List 생성후 반환
    private List<String> createCommentList(HashMap<String, Object> target) {
        List<String> comments = new ArrayList<String>();
        for (String key : target.keySet()) {
            HashMap<String, String> userInfo = (HashMap) target.get(key);
            comments.add(userInfo.get("comment"));
        }
        comments.stream().forEach(c -> Log.d(TAG, "createCommentList: " + c));

        return comments;
    }
    private void sort_comment(List<Comment> comment_list){
        for (Comment comment : comment_list){

        }
    }


    // 매개변수를 통해 City 객체의 각 변수들 초기화 이후 FirebaseDatabase에서 현재 도시의 기온 및 user들의 comment를 획득
    public void setInitInfo(String myCity, double latitude, double longitude, MainPresenterCallBack callBack) {
        this.myCity = myCity;
        this.latitude = latitude;
        this.longitude = longitude;

        // 현재 도시의 FirebaseDatabase 레퍼런스 획득
        DatabaseReference reference = firebaseDatabase.getReference(myCity);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Temperature 값 획득
                HashMap<String, Object> cityInfo = (HashMap) snapshot.getValue();
                temperature = (long) cityInfo.get("Temperature");

                // user의 comment 정보 획득
                HashMap<String, Object> users = (HashMap) cityInfo.get("users");
                List<String> commentList = createCommentList(users);
                callBack.onSuccess(commentList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    public void insert_comment(Comment comment, String city, String id) throws Exception {
        try {
            DatabaseReference reference = firebaseDatabase.getReference(city);
            reference.child("users").child(id).setValue(comment);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            throw new Exception();
        }
    }
}