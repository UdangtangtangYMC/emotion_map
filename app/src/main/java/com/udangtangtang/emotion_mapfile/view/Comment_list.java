package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.presenter.CommentListPresenter;

import java.util.ArrayList;

public class Comment_list extends Activity {
    private LinearLayout linearLayout;
    private TextView txt_city;
    private RecyclerView recyclerView;
    private CommentListPresenter commentListPresenter;
    private ArrayList<String> comment_list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentlist);

        Intent intent = getIntent();
        Comment_adapter comment_adapter = (Comment_adapter) intent.getSerializableExtra("com.udangtangtang.emotion_mapfile.adapter.Comment_adapter");
        initView((Boolean) intent.getSerializableExtra("isSunny"));
        commentListPresenter = new CommentListPresenter();

        setCityName();
        recyclerView.setAdapter(comment_adapter);


    }

    private void initView(boolean isSunny){
        //뷰 세팅
        linearLayout = findViewById(R.id.commentDetail_Linear);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        txt_city = findViewById(R.id.txt_city);

        if (isSunny) {
            linearLayout.setBackground(getResources().getDrawable(R.drawable.clear_sky,null));
        } else{
            linearLayout.setBackground(getResources().getDrawable(R.drawable.cloudy, null));
        }
    }

    private void setCityName() {
        txt_city.setText(commentListPresenter.getMyCity());
    }

}