package com.udangtangtang.emotion_mapfile.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

import java.security.cert.PKIXRevocationChecker;
import java.util.List;
import java.util.Optional;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private final String databaseURL = "https://emotion-map-312b1-default-rtdb.firebaseio.com";
    private final MainPresenter mainPresenter = new MainPresenter(new User(),new City());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);

        Log.d(TAG, "onCreate: called");
        mainPresenter.getCommentList();
        Log.d(TAG, "onCreate: finished");
    }
}