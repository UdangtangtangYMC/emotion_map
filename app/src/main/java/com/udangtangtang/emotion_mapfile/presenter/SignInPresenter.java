package com.udangtangtang.emotion_mapfile.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.EmailSignUpActivity;
import com.udangtangtang.emotion_mapfile.view.MainActivity;

public class SignInPresenter {
    private static final String TAG = "SignInPresenter";
    private Context context;
    private FirebaseAuth mAuth;

    public SignInPresenter(Context context, String key) {
        this.mAuth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void intent_EmailSignUpActivity() {
        Intent intent = new Intent(context, EmailSignUpActivity.class);
        context.startActivity(intent);
    }

    public void emailLogin(String id, String password) {
        if (id.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(id, password)
                    .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(context, MainActivity.class);
                                User login_user = User.getInstance();
                                login_user.setLogin_method("email");
                                login_user.setName(user.getDisplayName());
                                login_user.setID(user.getEmail());
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            } else {
                                Toast.makeText(context, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(context, "이메일과 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean check_autoLogin(String key) {
        boolean auto_checking = false;
        try {
            auto_checking = PreferenceManager.getBoolean(context, key);
        } catch (NullPointerException e) {
            setAutoLogin(key, false);
            return auto_checking;
        }
        return auto_checking;
    }

    public void setAutoLogin(String key, boolean value) {
        PreferenceManager.setBoolean(context, key, value);
        Log.d(TAG, "값세팅" + value);
    }
}
