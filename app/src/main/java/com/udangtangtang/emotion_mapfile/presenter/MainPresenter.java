package com.udangtangtang.emotion_mapfile.presenter;

import android.content.Context;

import com.udangtangtang.emotion_mapfile.view.PlusEmotion;

public class MainPresenter {
    private Context context;
    private PlusEmotion plusEmotion;

    public MainPresenter(Context context){
        this.context = context;
    }

    public void add_emotion(){
        plusEmotion = new PlusEmotion(context);
        plusEmotion.callFunciton();
    }
}
