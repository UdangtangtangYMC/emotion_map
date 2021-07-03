package com.udangtangtang.emotion_mapfile.presenter;

import android.content.Context;
import android.util.Log;

import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;

public class MainPresenter {

    private final String TAG = "MainPresenter";
    private final Context context;
    private PlusEmotion plusEmotion;
    private User user;
    private City city;

    public MainPresenter(Context context) {
        this.context = context;
        this.user = new User();
        this.city = new City();
    }

    public void getCommentList() {
        Log.d(TAG, "getCommentList: called");
        city.getCommentList("Anyang");
        Log.d(TAG, "getCommentList: finished");
    }

    public void add_emotion(){
        plusEmotion = new PlusEmotion(context);
        plusEmotion.callFunciton();
    }
}
