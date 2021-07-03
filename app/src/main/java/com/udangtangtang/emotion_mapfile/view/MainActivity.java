package com.udangtangtang.emotion_mapfile.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.domain.EmotionDTO;

import java.security.cert.PKIXRevocationChecker;
import java.util.Optional;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);

    }
}