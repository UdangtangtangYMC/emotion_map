package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.presenter.CommentListCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class City extends Thread{

    private final String TAG = "City";
    private final FirebaseDatabase firebaseDatabase;

    public City() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }


    public void getCommentList(String city, CommentListCallBack callBack) throws Exception{
        try {
            DatabaseReference reference = firebaseDatabase.getReference(city);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String,HashMap<String,String>> value = (HashMap) snapshot.getValue();
                    for (String key : value.keySet()) {
                        Log.d(TAG, "onDataChange: "+key+" : "+value.get(key));
                    }
                    callBack.onSuccess(createCommentList(value));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: ");
                }
            });
        }catch (Exception e) {
            throw new Exception();
        }
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

    public void insert_comment(Comment comment, String city, String id) throws Exception{
        try{
            DatabaseReference reference = firebaseDatabase.getReference(city);
            reference.child(id).setValue(comment);
        }catch(Exception e){
            Log.d(TAG, e.toString());
            throw new Exception();
        }
    }
}