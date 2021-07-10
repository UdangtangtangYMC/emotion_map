package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private long time = 0; // 뒤로가기 두 번 클릭 시 종료하기 위해 사용되는 변수
    private final String TAG = "MainActivity";
    private MainPresenter presenter;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout coordinatorLayout;
    private View drawerView;
    private ImageButton btn_plus; //감정 표시 버튼
    private TextView TextView_commentDetail, userCity, temperature, angry, happy,
            commentOne, commentTwo, commentThree, commentFour, recentStatus, recentComment, nationalStatistics;
    private ArrayList<TextView> commentViewList;

    private ImageButton btn_close, btn_logout;
    private TextView txt_id;

    //로그인 한 회원 정보 관련
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //위젯 연결
        initView();

        //presenter 생성 및 위치권한 요청
        Intent intent = getIntent();
        presenter = new MainPresenter(MainActivity.this, (User) intent.getSerializableExtra("user"));
        presenter.checkPermissions(this);

        //user 이름을 받아옴
        txt_id.setText(presenter.get_userName());

        //옆 메뉴 출력
        drawerLayout.setDrawerListener(listener);
        drawerLayout.setOnTouchListener((v, event) -> false);
        btn_close.setOnClickListener(v -> drawerLayout.closeDrawers());

        //감정 표시 버튼 클릭 시
        btn_plus.setOnClickListener(v -> presenter.add_emotion());

        //주변 상황 더보기 클릭시
        TextView_commentDetail.setOnClickListener(v -> presenter.intent_CommentDetail());

        // 통계 더보기 클릭시
        nationalStatistics.setOnClickListener(v->presenter.intent_nationalStatistics());

        //로그아웃 버튼 클릭 시
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 수행
                String loginMethod = presenter.getLoginMethod();
                switch (loginMethod) {
                    case "google":
                        presenter.logout_google(mAuth);
                        finish();
                        break;
                    case "kakao":
                        presenter.logout_kakao();
                        finish();
                        break;
                    default:
                        Log.d(TAG, "로그아웃 정보 얻어오기 실패");
                }
            }
        });
    }

    private void initView() {
        //뷰 세팅
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer);
        btn_plus = findViewById(R.id.btn_plus);
        btn_close = findViewById(R.id.btn_close);
        TextView_commentDetail = findViewById(R.id.textView_commentDetail);
        userCity = (TextView) findViewById(R.id.txt_userCity);
        temperature = (TextView) findViewById(R.id.txt_cityTemperature);
        TextView_commentDetail = findViewById(R.id.textView_commentDetail);
        angry = (TextView) findViewById(R.id.txt_angry);
        happy = (TextView) findViewById(R.id.txt_happy);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_coordinator);
        recentStatus = (TextView) findViewById(R.id.recent_status);
        recentComment = (TextView) findViewById(R.id.recent_comment);
        nationalStatistics = (TextView) findViewById(R.id.nationalStatisticsView);

        // comment를 보여줄 TextView
        commentOne = (TextView) findViewById(R.id.commentOne);
        commentTwo = (TextView) findViewById(R.id.commentTwo);
        commentThree = (TextView) findViewById(R.id.commentThree);
        commentFour = (TextView) findViewById(R.id.commentFour);

        // 4개의 view를 리스트에 추가
        commentViewList = new ArrayList<>();
        commentViewList.add(commentOne);
        commentViewList.add(commentTwo);
        commentViewList.add(commentThree);
        commentViewList.add(commentFour);

        //drawer
        txt_id = findViewById(R.id.txt_id);
        btn_logout = findViewById(R.id.btn_logout);
        //로그인 정보를 위한 변수 초기화
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "로그인 정보 불러오기 실패", Toast.LENGTH_SHORT).show();
        }
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

    // TextView에 텍스트 설정
    public void setInitInfo(List<String> commentList, Optional myStatus, Optional myComment) {
        userCity.setText(presenter.getUserCity());
        temperature.setText(getString(R.string.temperature,presenter.getCityTemperature()));
        angry.setText(getString(R.string.people, presenter.getAngryPeople()));
        happy.setText(getString(R.string.people, presenter.getHappyPeople()));

        if (myStatus.isPresent() && myComment.isPresent()) {
            recentStatus.setText(String.valueOf(myStatus.get()));
            recentComment.setText(String.valueOf(myComment.get()));
        } else{
            recentStatus.setHeight(0);
            recentComment.setText("지금 자신의 감정을 등록해보세요!");
        }

        if (commentList.size() == 0) {
            commentOne.setText("첫 번째 상태를 등록해보세요!");
        } else
            for (int i = 0; i < Math.min(commentList.size(), commentViewList.size()); i++) {
                commentViewList.get(i).setText(commentList.get(i));
            }
    }

    // 권한 설정 후 사용자의 결정에 따라 구문 실행
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: if statement entered");

            presenter.getLocality(this);
        } else {
            Snackbar
                    .make(coordinatorLayout, "권한 설정은 어플 재기동후 다시 설정하실 수 있습니다.", Snackbar.LENGTH_INDEFINITE)
                    .show();
        }
    }
}