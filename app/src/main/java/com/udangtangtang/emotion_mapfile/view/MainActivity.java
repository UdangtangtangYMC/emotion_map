package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

public class MainActivity extends Activity {
    private final String TAG = "MainActivity";
    private MainPresenter presenter;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private ImageButton btn_plus; //감정 표시 버튼
    private ImageButton btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        drawerLayout.setDrawerListener(listener);
        drawerLayout .setOnTouchListener((v, event) -> false);

        btn_close.setOnClickListener(v -> drawerLayout.closeDrawers());


        //감정 표시 버튼 클릭 시
        btn_plus.setOnClickListener(v -> presenter.add_emotion());
        presenter = new MainPresenter(MainActivity.this);
        presenter.getCommentList();
    }

    private void initView(){
        //뷰 세팅
        btn_plus = findViewById(R.id.btn_plus);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer);
        btn_close = findViewById(R.id.btn_close);
    }

    //메뉴창
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

}