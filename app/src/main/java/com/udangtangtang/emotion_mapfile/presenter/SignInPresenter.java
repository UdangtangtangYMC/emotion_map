package com.udangtangtang.emotion_mapfile.presenter;

import android.content.Context;
import android.content.Intent;

import com.udangtangtang.emotion_mapfile.view.EmailSignInActivity;
import com.udangtangtang.emotion_mapfile.view.EmailSignUpActivity;

public class SignInPresenter {
    private Context context;

    public SignInPresenter(Context context){
        this.context = context;
    }
    public void intent_EmailSignUpActivity(){
        Intent intent = new Intent(context, EmailSignUpActivity.class);
        context.startActivity(intent);
    }

    public void intent_EmailSignInActivity(){
        Intent intent = new Intent(context, EmailSignInActivity.class);
        context.startActivity(intent);
    }

}
