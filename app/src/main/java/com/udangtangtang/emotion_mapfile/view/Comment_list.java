package com.udangtangtang.emotion_mapfile.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.presenter.CommentListPresenter;
import com.udangtangtang.emotion_mapfile.presenter.Refreshable;

public class Comment_list extends AppCompatActivity implements Refreshable {
    private final String TAG = "Comment_list";
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private CommentListPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentlist);
        presenter = new CommentListPresenter(this, this);

        Intent intent = getIntent();
        //toolbar 세팅
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getResources().getString(R.string.myCity, presenter.getUserCity()));


        Comment_adapter comment_adapter = (Comment_adapter) intent.getSerializableExtra("com.udangtangtang.emotion_mapfile.adapter.Comment_adapter");
        initView((Boolean) intent.getSerializableExtra("isSunny"));

        recyclerView.setAdapter(comment_adapter);
    }

    private void initView(boolean isSunny) {
        //뷰 세팅
        linearLayout = findViewById(R.id.commentDetail_Linear);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Log.d(TAG, "initView: " + isSunny);
        if (isSunny) {
            linearLayout.setBackground(getResources().getDrawable(R.drawable.clear_sky, null));
            setStatusBarColor(getResources().getColor(R.color.clearSky_upper_gradient, null));
        } else {
            linearLayout.setBackground(getResources().getDrawable(R.drawable.cloudy, null));
            setStatusBarColor(getResources().getColor(R.color.cloudy_upper_gradient, null));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_titlebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_plus:
                presenter.add_emotion(this);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh() {
        //presenter.updateCommentDetailItems();
    }

    private void setStatusBarColor(int color) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
    }
}