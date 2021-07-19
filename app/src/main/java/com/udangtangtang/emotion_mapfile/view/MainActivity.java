package com.udangtangtang.emotion_mapfile.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private long time = 0; // 뒤로가기 두 번 클릭 시 종료하기 위해 사용되는 변수
    private final String TAG = "MainActivity";
    private MainPresenter presenter;
    private LinearLayout linearLayout, cityLayout, weatherLayout;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout coordinatorLayout;
    private ImageView weatherIcon;
    private TextView TextView_menu2, TextView_menu3, userCity, textViewTemperature, angry, happy,
            commentOne, commentTwo, commentThree, commentFour, recentStatus, recentComment;
    private ArrayList<TextView> commentViewList;
    private ImageButton btn_logout;
    private TextView txt_id;
    private SwipeRefreshLayout swipeRefresh;
    //로그인 한 회원 정보 관련
    private FirebaseAuth mAuth;
    //색상
    private int clearSky;
    private int cloudy;

    public int temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar 세팅
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        //위젯 연결
        initView();

        //presenter 생성 및 위치권한 요청
        Intent intent = getIntent();
        if(presenter == null) {
            presenter = new MainPresenter(MainActivity.this, MainActivity.this);
            presenter.checkPermissions(this);
        }

        //user 이름을 받아옴
        Log.d(TAG, "onCreate: presenter "+presenter);
        txt_id.setText(presenter.get_userName());

        //옆 메뉴 출력
        drawerLayout.setDrawerListener(listener);
        drawerLayout.setOnTouchListener((v, event) -> false);

        //주변 상황 더보기 클릭시
        TextView_menu2.setOnClickListener(v -> presenter.intent_CommentDetail());

        //지역별 통계 더보기 클릭시
        TextView_menu3.setOnClickListener(v -> presenter.intent_NationalStatistics());

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

        // 스와이프 새로고침
        swipeRefresh.setOnRefreshListener(
                () -> {
                    Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefresh.setRefreshing(false);
                        }
                    }, 500);
                    refresh();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_titlebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_plus:
                presenter.add_emotion();
                break;
            default:
        }
        return true;
    }

    private void initView() {
        //뷰 세팅
        drawerLayout = findViewById(R.id.drawer_layout);
        TextView_menu2 = findViewById(R.id.textView_menu2);
        TextView_menu3 = findViewById(R.id.textView_menu3Detail);
        userCity = (TextView) findViewById(R.id.txt_userCity);
        textViewTemperature = (TextView) findViewById(R.id.txt_cityTemperature);
        angry = (TextView) findViewById(R.id.txt_angry);
        happy = (TextView) findViewById(R.id.txt_happy);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_coordinator);
        recentStatus = (TextView) findViewById(R.id.recent_status);
        recentComment = (TextView) findViewById(R.id.recent_comment);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        cityLayout = (LinearLayout) findViewById(R.id.cityLayout);
        weatherLayout = (LinearLayout) findViewById(R.id.icon_layout);

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

        linearLayout = findViewById(R.id.linearLayout);

        //drawer
        btn_logout = findViewById(R.id.btn_logout);
        txt_id = findViewById(R.id.txt_id);
        btn_logout = findViewById(R.id.btn_logout);
        //로그인 정보를 위한 변수 초기화
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "로그인 정보 불러오기 실패", Toast.LENGTH_SHORT).show();
        }

        clearSky = getResources().getColor(R.color.clearSky_upper_gradient, null);
        cloudy = getResources().getColor(R.color.cloudy_upper_gradient, null);
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
            finishAffinity();
        }
    }

    public void refresh() {
        this.linearLayout.removeAllViewsInLayout();
        presenter.checkPermissions(this);
    }

    public void setComments(Optional<List<Comment>> comments) {
        comments.ifPresent(c -> {
            for (int i = 0; i < Math.min(c.size(), commentViewList.size()); i++) {
                commentViewList.get(i).setText(getString(R.string.comment, c.get(i).getStatus(), c.get(i).getComment()));
            }
        });

        if (!comments.isPresent()) {
            commentOne.setText("첫 번째 상태를 등록해보세요!");
        }
    }

    //표 텍스트 설정
    public void setChart(String name, int angry_count, int happy_count, int total, int index) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        //한 행에 들어갈 textView 4개 생성
        TextView[] textViews = {new TextView(this), new TextView(this), new TextView(this), new TextView(this)};

        for (int i = 0; i < textViews.length; i++) {
            textView_setting(textViews[i], index);
        }

        textViews[0].setText(name);
        textViews[1].setText(String.valueOf(angry_count + "명"));
        textViews[2].setText(String.valueOf(happy_count + "명"));
        textViews[3].setText(String.valueOf(total + "명"));

        for (TextView textView : textViews) {
            linearLayout.addView(textView);
        }
        this.linearLayout.addView(linearLayout);
    }

    private void textView_setting(TextView textview, int index) {
        //TextView 속성 설정을 위한 layoutParams 생성
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 105, 1);
        layoutParams.leftMargin = 14;
        layoutParams.bottomMargin = 5;
        textview.setLayoutParams(layoutParams);
        textview.setGravity(17);
        textview.setTextColor(getResources().getColor(R.color.white, null));
        if (index % 2 == 0) {
            textview.setTextColor(getResources().getColor(R.color.black, null));
            textview.setBackground(ContextCompat.getDrawable(this, R.drawable.round_border));
        }
        textview.setPadding(3, 3, 3, 3);
    }

    // 권한 설정 후 사용자의 결정에 따라 구문 실행
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: if statement entered");

            presenter.getLocality(this);
        } else {
            Snackbar
                    .make(coordinatorLayout, "권한 설정은 어플 재기동후 다시 설정하실 수 있습니다.", Snackbar.LENGTH_INDEFINITE)
                    .show();
        }
    }

    public void setCityStats(String temperature, String happyPeople, String angryPeople) {

        // 필요한 drawable 객체 선언
        Drawable clearSky = getResources().getDrawable(R.drawable.clear_sky, null);
        Drawable cloudy = getResources().getDrawable(R.drawable.cloudy, null);
        this.temperature = Integer.parseInt(temperature);

        // 기온이 0도 보다 높으면 clearSky 적용, 0도 이하이면 cloudy 적용
        if (Integer.parseInt(temperature) > 0) {
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny_icon, null));
            drawerLayout.setBackground(clearSky);
            setStatusBarColor(this.clearSky);
        } else {
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy_icon, null));
            weatherIcon.setScaleX(Float.parseFloat("1.5"));
            weatherIcon.setScaleY(Float.parseFloat("1.5"));
            drawerLayout.setBackground(clearSky);
            setStatusBarColor(this.cloudy);
        }

        Log.d(TAG, "setCityStats: cityLayout" + cityLayout.getMeasuredHeight());
        Log.d(TAG, "setCityStats: weatherLayout" + weatherLayout.getMeasuredHeight());
        Log.d(TAG, "setCityStats: weatherIcon" + weatherIcon.getMeasuredHeight());
        userCity.setText(presenter.getUserCity());
        textViewTemperature.setText(getString(R.string.temperature, temperature));
        happy.setText(getString(R.string.people, happyPeople));
        angry.setText(getString(R.string.people, angryPeople));
    }

    private void setStatusBarColor(int color) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
    }

    public void setMyRecentComment(Optional<String> status, Optional<String> comment) {
        if (status.isPresent() && comment.isPresent()) {
            recentStatus.setHeight(80);
            recentStatus.setText(status.get());
            recentComment.setText(comment.get());
            Log.d(TAG, "params : " + status.get() + comment.get());

        } else {
            recentStatus.setHeight(0);
            recentComment.setText("지금 당신의 상태를 기록해보세요!");
        }

    }

    public void setMyRecentComment(String s, String s1) {
        recentStatus.setText(s);
        recentComment.setText(s1);
    }
}