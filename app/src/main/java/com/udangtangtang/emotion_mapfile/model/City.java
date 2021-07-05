package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.presenter.CommentListCallBack;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class City extends Thread{

    private final String TAG = "City";
    private final FirebaseDatabase firebaseDatabase;
    private String city;

    public City() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void getCommentList(String city, CommentListCallBack callBack){
        new Thread(){
            public void run(){
                DatabaseReference reference = firebaseDatabase.getReference(city);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String,HashMap<String,String>> value = (HashMap) snapshot.getValue();
                        for (String key : value.keySet()) {
                            Log.d(TAG, "onDataChange: "+key+" : "+value.get(key));
                        }
                        callBack.onSuccess(createCommentList(value));
                        Log.d(TAG, "2");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: ");
                    }
                });
            }
        }.start();
    }




    private List<String> createCommentList(HashMap<String, HashMap<String,String>> target){
        List<String> comments = new ArrayList<String>();
        for (String key : target.keySet()) {
            HashMap<String, String> userInfo = target.get(key);
            comments.add(userInfo.get("comment"));
        }
        comments.stream().forEach(c-> Log.d(TAG, "createCommentList: "+c));
        Log.d(TAG, "1");

        return comments;
    }


}