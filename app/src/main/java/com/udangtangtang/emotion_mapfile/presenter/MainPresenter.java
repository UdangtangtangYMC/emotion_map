package com.udangtangtang.emotion_mapfile.presenter;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.MainActivity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MainPresenter {

    private final String TAG = "MainPresenter";

    private final User user;
    private final City city;

    public void getCommentList(){
        Log.d(TAG, "getCommentList: called");
        city.getCommentList("Anyang");
        Log.d(TAG, "getCommentList: finished");
    }

}
