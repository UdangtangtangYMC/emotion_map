package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.presenter.CommentListPresenter;

import java.util.ArrayList;

public class Comment_list extends Activity {
    private TextView txt_city;
    private RecyclerView recyclerView;
    private Comment_adapter comment_adapter;
    private CommentListPresenter commentListPresenter;
    private ArrayList<String> comment_list = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentlist);

        initView();

        Intent intent = getIntent();
        comment_adapter = (Comment_adapter) intent.getSerializableExtra("adapter");
        recyclerView.setAdapter(comment_adapter);

    }

    private void initView(){
        //뷰 세팅
       recyclerView = findViewById(R.id.recyclerview);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       recyclerView.setHasFixedSize(true);
       txt_city = findViewById(R.id.txt_city);
    }

}
