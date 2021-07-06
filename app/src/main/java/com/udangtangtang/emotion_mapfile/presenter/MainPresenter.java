package com.udangtangtang.emotion_mapfile.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.Comment_list;
import com.udangtangtang.emotion_mapfile.view.MainActivity;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import lombok.Synchronized;

public class MainPresenter {

    private final String TAG = "MainPresenter";
    private final Context context;
    private PlusEmotion plusEmotion;
    private User user;
    private City city;
    private Comment_adapter comment_adapter;
    private RecyclerView comment_view;


    public MainPresenter(Context context, RecyclerView comment_view, User user) {
        this.context = context;
        this.user = user;
        this.city = new City();
        this.comment_view = comment_view;
    }

    public void set_locInfo(){

    }

    public String get_userName(){
        Log.d(TAG, user.getName());
        return user.getName();
    }

    public void add_emotion(){
        PlusEmotionPresenter plusEmotionPresenter = new PlusEmotionPresenter(context, city, user);
        plusEmotion = new PlusEmotion(context, plusEmotionPresenter);
        plusEmotion.callFunciton(new RefreshCallBack(){
            @Override
            public void refresh() {
                insert_CommentList();
            }
        });
    }


    public void insert_CommentList(){
        //db로 부터 데이터를 받아오고 입력한 city를 기반으로한 data 배열 생성
        //커멘트 어댑터 생성 및 데이터 recyclerview에 들어갈 data set
        try {
            city.getCommentList("Anyang", new CommentListCallBack() {
                @Override
                public void onSuccess(List<String> comment_list) {
                    comment_adapter = new Comment_adapter(comment_list);
                    comment_view.setAdapter(comment_adapter);
                }

                @Override
                public void onFail(Exception ex) throws Exception {
                    throw new Exception();
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "멘트 목록 불러오기 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public void intent_CommentDetail(){
        if(comment_adapter != null){
            Intent intent = new Intent(context, Comment_list.class);
            intent.putExtra("adapter", comment_adapter);
            context.startActivity(intent);
        }else{
            Toast.makeText(context, "멘트 목록 상세보기 실패", Toast.LENGTH_SHORT).show();
        }
    }
}
