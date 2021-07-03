package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.domain.EmotionDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.Value;

public class City {

    private final String TAG = "City";
    private final FirebaseDatabase firebaseDatabase;

    public City() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void getCommentList(String city){
        DatabaseReference reference = firebaseDatabase.getReference(city);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,HashMap<String,String>> value = (HashMap) snapshot.getValue();
                for (String key : value.keySet()) {
                    Log.d(TAG, "onDataChange: "+key+" : "+value.get(key));
                }
                createCommentList(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    private void createCommentList(HashMap<String, HashMap<String,String>> target){
        ArrayList<String> comments = new ArrayList<>();
        for (String key : target.keySet()) {
            HashMap<String, String> userInfo = target.get(key);
            comments.add(userInfo.get("comment"));
        }
        comments.stream().forEach(c-> Log.d(TAG, "createCommentList: "+c));
    }
}
