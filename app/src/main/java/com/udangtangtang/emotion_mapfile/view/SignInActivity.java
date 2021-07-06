package com.udangtangtang.emotion_mapfile.view;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

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
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.udangtangtang.emotion_mapfile.R;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.presenter.SignInPresenter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class SignInActivity extends Activity {
    private final String TAG = "SignInActivity";
    private Button signUpButton;
    private Button signInButton;
    private SignInButton googleSignInButton;
    private ImageButton kakaoSignInButton;
    private SignInPresenter signInPresenter;

    //구글 로그인을 위해 필요한 변수 선언
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    //카카오 로그인을 위해 필요한 변수 선언
    private Function2<OAuthToken, Throwable, Unit> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainaftersplash);

        initView();

        SignInPresenter signInPresenter = new SignInPresenter(SignInActivity.this);

        Log.d("GET_KEYHASH", getKeyHash());

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
                if(LoginClient.getInstance().isKakaoTalkLoginAvailable(SignInActivity.this)){
                    LoginClient.getInstance().loginWithKakaoTalk(SignInActivity.this, callback);
                }else{
                    LoginClient.getInstance().loginWithKakaoAccount(SignInActivity.this, callback);
                }
            }
        });

    }

    private void initView(){
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        kakaoSignInButton = findViewById(R.id.kakaoSignInButton);
        try{
            callback=new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if(oAuthToken!=null){

                    }
                    if(throwable!=null){

                    }
                    //updateaKakaoLoginUI();
                    return null;
                }
            };
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Kakao_login: request oAuthToken Fail", Toast.LENGTH_SHORT).show();
        }
    }

    public void login_google(){
        //mAuth - firebaseAuth를 사용하기 위해 인스턴스를 꼭 받아와야함
        try{
            mAuth = FirebaseAuth.getInstance();
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

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
            }
        }
    }

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
                            Log.d(TAG, "5");
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Log.d(TAG, "5");
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            User login_user = new User();
            try{
                login_user.setName(user.getDisplayName());
                login_user.setID(user.getEmail());
                intent.putExtra("user",login_user);
                this.startActivity(intent);
                finish();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "google_login: request user_name fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getKeyHash() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null) return null;
            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    Log.w("getKeyHash", "Unable to get MessageDigest. signature=" + signature, e);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("getPackageInfo", "Unable to getPackageInfo");
        }
        return null;
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