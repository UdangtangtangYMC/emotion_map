package com.udangtangtang.emotion_mapfile.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.presenter.SessionCallback;
import com.udangtangtang.emotion_mapfile.presenter.SignInPresenter;


public class SignInActivity extends Activity {
    private long time = 0;
    private final String TAG = "SignInActivity";
    private Button signUpButton;
    private Button signInButton;
    private SignInButton googleSignInButton;
    private ImageButton kakaoSignInButton;
    private SignInPresenter signInPresenter;
    private FirebaseUser currentUser;


    //구글 로그인을 위해 필요한 변수 선언
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    //카카오 로그인을 위해 필요한 변수 선언
    private SessionCallback sessionCallback = new SessionCallback(SignInActivity.this);
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        signInPresenter = new SignInPresenter(SignInActivity.this);

        //카카오세션 초기화
        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        //회원가입
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signInPresenter.intent_EmailSignUpActivity();
            }
        });

        //로그인
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInPresenter.intent_EmailSignInActivity();
            }
        });

        //구글 로그인
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_google();
            }
        });

        //카카오 로그인
        kakaoSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Session.getCurrentSession().checkAndImplicitOpen()){
                    Log.d(TAG, "onClick: 로그인 세션 살아있음");
                    sessionCallback.requestMe();
                }else{
                    Log.d(TAG, "onClick: 로그인 세션 끝남");
                    session.open(AuthType.KAKAO_LOGIN_ALL,SignInActivity.this);
                }
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI_google(currentUser);
            finish();
        }else if(Session.getCurrentSession().checkAndImplicitOpen()){
            Log.d(TAG, "onStart: 로그인 세션 살아있음");
            sessionCallback.requestMe();
        }
    }

    private void initView(){
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        kakaoSignInButton = findViewById(R.id.kakaoSignInButton);
        mAuth = FirebaseAuth.getInstance();
    }

    //구글 로그인
    public void login_google(){
        //mAuth - firebaseAuth를 사용하기 위해 인스턴스를 꼭 받아와야함
        try{
            this.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            signIn();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "google_login: requestIdToken Fail", Toast.LENGTH_SHORT);
        }
    }

    //결과값을 SignInActivity 액티비티로 보냄
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //구글 client로 부터 결과값이 왔을 때 반응
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.d(TAG, "google_login : task fail");
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    //파이어베이스의 인증 기능 사용
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI_google(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI_google(null);
                        }
                    }
                });
    }

    //로그인 한 결과에 따른 UI업데이트
    private void updateUI_google(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            User login_user = new User();
            try{
                login_user.setLogin_method("google");
                login_user.setName(user.getDisplayName());
                login_user.setID(user.getEmail());
                Log.d(TAG, "updateUI_google: "+user.getEmail());
                intent.putExtra("user",login_user);
                this.startActivity(intent);
                finish();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "google_login: request user_name fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

       /*
    *kakao logout code
        btn_login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().logout(error -> {
                    if (error != null) {
                        Log.e(TAG, "로그아웃 실패, SDK에서 토큰 삭제됨", error);
                    }else{
                        Log.e(TAG, "로그아웃 성공, SDK에서 토큰 삭제됨");
                    }
                    return null;
                });
            }
        });
    }

});
     */

      /*
        logoutButton.setOnClickListner(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>(){
                    @Override
                    public Unit invoke(Throwable throwable){
                        //updateKakaoLoginUI();
                        return null;
                    }
                });
            }
        });
        */

}