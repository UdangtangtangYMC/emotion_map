package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

public class MainActivity extends Activity {

    private long time = 0; // 뒤로가기 두 번 클릭 시 종료하기 위해 사용되는 변수
    private final String TAG = "MainActivity";
    private MainPresenter presenter;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private RecyclerView comment_view;
    private ImageButton btn_plus; //감정 표시 버튼
    private ImageButton btn_close;
    private TextView TextView_commentDetail;
    private TextView userCity;
    private TextView temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //위젯 연결
        initView();

        //presenter 생성 및 위치권한 요청
        presenter = new MainPresenter(MainActivity.this);
        presenter.checkPermissions(this);

        //옆 메뉴 출력
        drawerLayout.setDrawerListener(listener);
        drawerLayout.setOnTouchListener((v, event) -> false);
        btn_close.setOnClickListener(v -> drawerLayout.closeDrawers());

        //감정 표시 버튼 클릭 시
        btn_plus.setOnClickListener(v -> presenter.add_emotion());

        //주변 상황 더보기 클릭시
        TextView_commentDetail.setOnClickListener(v -> presenter.intent_CommentDetail());
    }

    private void initView() {
        //뷰 세팅
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer);
        comment_view = findViewById(R.id.commentList);
        comment_view.setLayoutManager(new LinearLayoutManager(this));
        comment_view.setHasFixedSize(true);
        btn_plus = findViewById(R.id.btn_plus);
        btn_close = findViewById(R.id.btn_close);
        TextView_commentDetail = findViewById(R.id.textView_commentDetail);
        userCity = (TextView) findViewById(R.id.txt_userCity);
        temperature = (TextView) findViewById(R.id.txt_cityTemperature);
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

    //뒤로가기 버튼 2번을 통해 시스템 종료
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }

    public void setInitInfo(Comment_adapter adapter) {
        comment_view.setAdapter(adapter);
        userCity.setText(presenter.getUserCity());
        temperature.setText(presenter.getCityTemperature()+" ℃");
    }
}