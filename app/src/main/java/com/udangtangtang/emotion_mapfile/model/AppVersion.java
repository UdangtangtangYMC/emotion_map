package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.presenter.AppVersionCallBack;

import java.util.HashMap;
import java.util.Optional;

public class AppVersion {
    private final FirebaseDatabase firebaseDatabase;
    private final String TAG = "AppVersion";

    public AppVersion() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void getVersion(AppVersionCallBack callBack){
        Log.d(TAG, "getVersion");
        DatabaseReference users = firebaseDatabase.getReference("AppVersion");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Log.d(TAG, "getVersion2");
                    // snapshot으로부터 데이터를 Optional 형태로 획득
                    Optional<HashMap<String, Object>> version = Optional.ofNullable((HashMap) snapshot.getValue());
                    // version 안에 데이터가 존재한다면 callBack을 통해 comments안의 데이터를 기반으로 Comment 객체로 이루어져 있는 List를 매개변수로 전달
                    version.ifPresent(u -> callBack.onSuccess(u));
                    if (!version.isPresent()) {
                        // 데이터가 없다면 onFailed() 호출
                        callBack.onFail();
                    }
                } catch (NullPointerException e) {
                    callBack.onFail();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }
}
