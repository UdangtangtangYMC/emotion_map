package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenter;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private long time = 0; // 뒤로가기 두 번 클릭 시 종료하기 위해 사용되는 변수
    private final String TAG = "MainActivity";
    private MainPresenter presenter;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private ImageButton btn_plus; //감정 표시 버튼
    private TextView TextView_commentDetail, userCity, temperature, angry, happy, commentOne, commentTwo, commentThree,commentFour;
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
        presenter = new MainPresenter(MainActivity.this, (User)intent.getSerializableExtra("user"));
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

        //로그아웃 버튼 클릭 시
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 수행
                String loginMethod = presenter.getLoginMethod();
                switch (loginMethod){
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
        try{
            mAuth = FirebaseAuth.getInstance();
        }catch (Exception e){
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

    // TextView에 텍스트 설정 및, RecyclerView에 어댑터 설정
    public void setInitInfo(ArrayList<String> commentList) {
        userCity.setText(presenter.getUserCity());
        temperature.setText(presenter.getCityTemperature()+" ℃");
        angry.setText(presenter.getAngryPeople()+"명");
        happy.setText(presenter.getHappyPeople()+"명");

        if (commentList.size() == 0) {
            commentOne.setText("첫 번째 상태를 등록해보세요!");
            return;
        }

        for (int i = 0; i < commentList.size(); i++) {
            commentViewList.get(i).setText(commentList.get(i));
        }
    }
}