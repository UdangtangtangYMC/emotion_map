package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.content.Intent;
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

public class EmailSignInActivity extends Activity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth= FirebaseAuth.getInstance();
        findViewById(R.id.signInSubmitButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.signInSubmitButton:
                    signIn();
                    break;
            }
        }
    };

    private void signIn() {
        String email = ((EditText) findViewById(R.id.signInEmailInput)).getText().toString();
        String password = ((EditText) findViewById(R.id.signInPasswordInput)).getText().toString();
        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(EmailSignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent=new Intent(EmailSignInActivity.this, SignInActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(EmailSignInActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        else{
            Toast.makeText(EmailSignInActivity.this, "이메일과 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
