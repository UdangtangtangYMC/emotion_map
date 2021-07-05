package com.udangtangtang.emotion_mapfile.presenter;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        KakaoSdk.init(this,"14c0eb02333568e599c669cb59b7e30c");
    }
}
