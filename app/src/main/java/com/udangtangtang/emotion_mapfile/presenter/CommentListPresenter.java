package com.udangtangtang.emotion_mapfile.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.Comment_list;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;

public class CommentListPresenter {
    private final City city;
    private final User user;
    private Context context;
    private Comment_list activity;

    public CommentListPresenter(Context context, Comment_list activity) {
        this.city = City.getInstance();
        this.user = User.getInstance();
        this.context = context;
        this.activity = activity;
    }

    public String getMyCity() {
        return city.getMyCity();
    }

    public void add_emotion(Refreshable refreshable) {
        if (user.getID() == null) {
            Toast.makeText(context, "Kakao login : 이메일 정보제공에 동의하여야 합니다.", Toast.LENGTH_SHORT).show();
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "위치 권한 사용에 동의하여야 합니다.", Toast.LENGTH_SHORT).show();
        } else {
            PlusEmotionPresenter plusEmotionPresenter = PlusEmotionPresenter.getInstance(context, city, user);
            PlusEmotion plusEmotion = new PlusEmotion(context, plusEmotionPresenter, refreshable);
            plusEmotion.show(activity.getSupportFragmentManager(),"plus_emotion");
        }
    }
}
