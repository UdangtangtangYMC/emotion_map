package com.udangtangtang.emotion_mapfile.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.audiofx.PresetReverb;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private long time = 0; // 뒤로가기 두 번 클릭 시 종료하기 위해 사용되는 변수
    private final String TAG = "MainActivity";
    private MainPresenter presenter;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private RecyclerView comment_view;
    private ImageButton btn_plus; //감정 표시 버튼
    private ImageButton btn_close;
    private TextView TextView_commentDetail;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //위젯 연결
        initView();

        //presenter 생성 및 위치권한 요청
        presenter = new MainPresenter(MainActivity.this);
        presenter.checkPermissions(MainActivity.this,this);

        //옆 메뉴 출력
        drawerLayout.setDrawerListener(listener);
        drawerLayout.setOnTouchListener((v, event) -> false);
        btn_close.setOnClickListener(v -> drawerLayout.closeDrawers());

        //recyclerview 세팅
        //presenter를 통해 받아온 adapter 객체를 set
        presenter.insert_CommentList(comment_view);

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
}