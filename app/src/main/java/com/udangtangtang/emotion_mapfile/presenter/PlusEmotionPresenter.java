package com.udangtangtang.emotion_mapfile.presenter;

import android.content.Context;

import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.Comment;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlusEmotionPresenter {
    private Context context;
    private City city;
    private User user;

    public PlusEmotionPresenter(Context context, City city, User user){
        this.context = context;
        this.city = city;
        this.user = user;
    }

    public String get_emotion(int id){ return (id==0) ? "기쁨" : "슬픔"; }

    public void insert_emotion(String seleted_emotion, String comment){
        Comment input_comment = new Comment();

        input_comment.setComment(comment);
        input_comment.setStatus(seleted_emotion);
        input_comment.setCreate_at(get_date());
        //후에 api를 통해 받아옴
        input_comment.setDistrict("만안구");
        input_comment.setLatitude(37.400303);
        input_comment.setLogitude(126.102);

        //삭제 예정 코드임 user객체는 ID city는 로그인시 객체생성을통해 삽입
        user.setCity("Anyang");
        user.setUserID("test111");
        city.insert_comment(input_comment, user.getCity(), user.getUserID());
    }

    public String get_date(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date time = new Date();

        String date = format.format(time);

        return date;
    }
}
