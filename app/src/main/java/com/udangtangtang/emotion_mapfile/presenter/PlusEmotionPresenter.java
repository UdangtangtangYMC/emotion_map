package com.udangtangtang.emotion_mapfile.presenter;

import android.content.Context;
import android.widget.Toast;

import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.Comment;
import com.udangtangtang.emotion_mapfile.model.User;

import java.util.Date;
import java.util.Optional;

public class PlusEmotionPresenter {
    private Context context;
    private final City city;
    private final User user;

    public PlusEmotionPresenter(Context context, City city, User user){
        this.context = context;
        this.city = city;
        this.user = user;
    }

    public String get_emotion(int id, int happy_id){ return (id==happy_id) ? "기쁨" : "빡침"; }

    public void insert_emotion(String selected_emotion, String comment){
        Comment input_comment = new Comment();

        input_comment.setComment(comment);
        input_comment.setStatus(selected_emotion);
        input_comment.setCreate_at(get_date());

        try {
            // 새로 등록하려는 comment와 user.getID()를 매개변수로 city.insertComment 메소드 호출
            city.insert_comment(input_comment, user.getID(), createInsertCommentCallBack());
        } catch (Exception e) {
            Toast.makeText(context, "감정 입력 오류", Toast.LENGTH_SHORT).show();
        }
    }

    public Long get_date(){
        Date time = new Date();
        return time.getTime();
    }

    private InsertCommentCallBack createInsertCommentCallBack(){
        return new InsertCommentCallBack() {
            @Override
            public void onSuccess(Optional<Boolean> statusChanged, String status) {
                city.changeStatus(statusChanged, status);
            }
        };
    }
}
