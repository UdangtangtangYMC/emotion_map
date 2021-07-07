package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenterCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class City {

    private final String TAG = "City";
    private final FirebaseDatabase firebaseDatabase;
    private double latitude;
    private double longitude;
    private String myCity;
    private long temperature;

    public City() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private List<String> createCommentList(HashMap<String, Object> target){
        List<String> comments = new ArrayList<String>();
        for (String key : target.keySet()) {
            HashMap<String, String> userInfo = (HashMap) target.get(key);
            comments.add(userInfo.get("comment"));
        }
        comments.stream().forEach(c-> Log.d(TAG, "createCommentList: "+c));

        return comments;
    }

    public void setInitInfo(String myCity, double latitude, double longitude, MainPresenterCallBack callBack) {
        this.myCity = myCity;
        this.latitude = latitude;
        this.longitude = longitude;

        DatabaseReference reference = firebaseDatabase.getReference(myCity);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object> cityInfo = (HashMap) snapshot.getValue();
                temperature = (long) cityInfo.get("Temperature");

                HashMap<String,Object> users = (HashMap) cityInfo.get("users");
                List<String> commentList = createCommentList(users);
                callBack.onSuccess(commentList);

                    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }
}