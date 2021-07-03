package com.udangtangtang.emotion_mapfile.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

public class MainActivity extends Activity {
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
        drawerLayout .setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });


        //감정 표시 버튼 클릭 시
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.add_emotion();
            }
        });
    }

    private void initView(){
        //뷰 세팅
        btn_plus = findViewById(R.id.btn_plus);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer);
        btn_close = findViewById(R.id.btn_close);
    }

    @Override
    protected void onResume() {
        //presenter 실행
        super.onResume();
        presenter = new MainPresenter(MainActivity.this);
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