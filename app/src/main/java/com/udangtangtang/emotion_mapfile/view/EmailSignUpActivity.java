package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.udangtangtang.emotion_mapfile.R;

import java.util.Locale;

public class EmailSignUpActivity extends Activity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale m = getResources().getConfiguration().locale;
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.signUpSubmitButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.signUpSubmitButton:
                    signUp();
                    break;
            }
        }
    };

    private void signUp() {
        String name = ((EditText) findViewById(R.id.nameInput)).getText().toString();
        String email = ((EditText) findViewById(R.id.emailInput)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordInput)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.passwordRepeatInput)).getText().toString();

        if (name.length() > 0 && email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) {
                // 사용자가 입력한 이메일과 비밀번호를 Firebase에 저장
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EmailSignUpActivity.this, "회원가입 되었습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (task.getException().toString() != null) {
                                        if (password.length() < 6) {
                                            Toast.makeText(EmailSignUpActivity.this, "6자리 이상의 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (email.length()==0){
                                            Toast.makeText(EmailSignUpActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(EmailSignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });
            } else {
                Toast.makeText(EmailSignUpActivity.this, "비밀번호를 동일하게 입력했나요?", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EmailSignUpActivity.this, "모두 입력했나요?", Toast.LENGTH_SHORT).show();
        }
    }
}
