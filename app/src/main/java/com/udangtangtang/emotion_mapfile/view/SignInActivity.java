package com.udangtangtang.emotion_mapfile.view;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.util.helper.Utility;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.presenter.SessionCallback;
import com.udangtangtang.emotion_mapfile.presenter.SignInPresenter;


public class SignInActivity extends Activity {
    //구글 로그인을 위해 필요한 변수 선언
    private static final int RC_SIGN_IN = 9001;
    private final String TAG = "SignInActivity";
    private final String key = "Auto_login";
    Session session;
    private long time = 0;
    private Button signUpButton;
    private Button signInButton;
    private ImageButton googleSignInButton;
    private ImageButton kakaoSignInButton;
    private SignInPresenter signInPresenter;
    private FirebaseUser currentUser;
    private EditText edt_email, edt_password;
    private CheckBox check_autoLogin;
    private boolean check_auto;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    //카카오 로그인을 위해 필요한 변수 선언
    private SessionCallback sessionCallback = new SessionCallback(SignInActivity.this);

    //앱 버전 체크
    private final int REQUEST_CODE = 3247;
    private final int RESULT_OK=Activity.RESULT_OK;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
        updateCheck();

        signInPresenter = new SignInPresenter(this,SignInActivity.this, key);
        //구글 세션 초기화
        currentUser = mAuth.getCurrentUser();
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
                signInPresenter.setAutoLogin(key, check_auto);
                String id = edt_email.getText().toString();
                String password = edt_password.getText().toString();
                signInPresenter.emailLogin(id, password);
            }
        });

        //구글 로그인
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInPresenter.setAutoLogin(key, check_auto);
                login_google();
            }
        });

        //카카오 로그인
        kakaoSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInPresenter.setAutoLogin(key, check_auto);
                if (Session.getCurrentSession().checkAndImplicitOpen()) {
                    Log.d(TAG, "onClick: 로그인 세션 살아있음");
                    sessionCallback.requestMe();
                } else {
                    Log.d(TAG, "onClick: 로그인 세션 끝남");
                    session.open(AuthType.KAKAO_LOGIN_ALL, SignInActivity.this);
                }
            }
        });

        //자동로그인 리스너
        check_autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check_auto = isChecked;
                Log.d(TAG, "체크박스 리스너 : " + check_auto);
            }
        });

        String language = getResources().getConfiguration().getLocales().get(0).getLanguage();
        if (!language.equals("ko")) {
            disableAll();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        get_checkAutoLogin();
        if (check_auto) {
            check_autoLogin.setChecked(true);
            checkSession();
        }
    }

    private void updateCheck(){
        //인앱버전 업데이트 체크
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        //업데이트를 체크하는데 사용되는 인텐트를 리턴한다.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        //checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo->{ //appUpdateManager이 추가되는데 성공하면 발생하는 이벤트
            if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // UpdateAvailability.UPDATE_AVAILABLE == 2이면 앱 true
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){ //허용된 타입의 앱 업데이트면 실행
                requestUpdate(appUpdateInfo);
            }
        });

    }
    private void requestUpdate(AppUpdateInfo appUpdateInfo){
        try{
            appUpdateManager.startUpdateFlowForResult(
                    //getAppUpdateInfo() 에 의해 리턴된 인텐트
                    appUpdateInfo,
                    //AppUpdateType.FLEXIBLE : 사용자에게 업데이트 여부를 물은 후 업데이트 실행 가능
                    //AppUpdateType.IMMEDIATE : 사용자가 수락해야만 하는 업데이트 창을 보여줌
                    AppUpdateType.IMMEDIATE,
                    // 현재 업데이트 요청을 만든 액티비티
                    this,
                    REQUEST_CODE);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void initView() {
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        kakaoSignInButton = findViewById(R.id.kakaoSignInButton);
        edt_email = findViewById(R.id.signInEmailInput);
        edt_password = findViewById(R.id.signInPasswordInput);
        mAuth = FirebaseAuth.getInstance();
        check_autoLogin = findViewById(R.id.check_autoLogin);

    }

    //자동로그인 여부 확인
    private void get_checkAutoLogin() {
        this.check_auto = signInPresenter.check_autoLogin(key);
        Log.d(TAG, "자동로그인 확인 " + check_auto);
    }

    private void checkSession() {
        if (currentUser != null) {
            updateUI_google(currentUser);
            finish();
        } else if (Session.getCurrentSession().checkAndImplicitOpen()) {
            Log.d(TAG, "onStart: 로그인 세션 살아있음");
            sessionCallback.requestMe();
        }
    }

    //구글 로그인
    public void login_google() {
        //mAuth - firebaseAuth를 사용하기 위해 인스턴스를 꼭 받아와야함
        try {
            this.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            signIn();
        } catch (Exception e) {
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
        }else if(requestCode == REQUEST_CODE){
            if(resultCode != RESULT_OK){
                Log.d(TAG, "Update flow failed! Result");
                com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                        //업데이트 재요청
                        requestUpdate(appUpdateInfo);
                    }
                });
            }
        }

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateINfo -> {
                            if (appUpdateINfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                                try{
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateINfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,
                                            REQUEST_CODE);
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(), "업데이트 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            User login_user = User.getInstance();
            try {
                login_user.setLogin_method("google");
                login_user.setName(user.getDisplayName());
                login_user.setID(user.getEmail());
                login_user.setEmail(user.getEmail());
                Log.d(TAG, "updateUI_google: " + user.getEmail());
                this.startActivity(intent);
                finish();
            } catch (Exception e) {
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
            moveTaskToBack(true);
            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void disableAll() {
        Toast.makeText(SignInActivity.this, "시스템 언어가 한국어가 아닙니다.", Toast.LENGTH_LONG).show();
        signUpButton.setEnabled(false);
        signInButton.setEnabled(false);
        googleSignInButton.setEnabled(false);
        kakaoSignInButton.setEnabled(false);
        edt_email.setEnabled(false);
        edt_password.setEnabled(false);
        check_autoLogin.setEnabled(false);
    }
}