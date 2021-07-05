package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.signInActivity;

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_TIME=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                startActivity(new Intent(getApplication(), signInActivity.class));
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_TIME);
    }

    @Override
    public void onBackPressed() {}
}
