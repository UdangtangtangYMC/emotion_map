package com.udangtangtang.emotion_mapfile.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.model.Comment;

import java.io.Serializable;
import java.util.List;

public class Comment_adapter extends RecyclerView.Adapter<Comment_adapter.MyViewHolder> implements Serializable {
    private final String TAG = "Comment_adapter";
    private List<Comment> comment_list;
    private boolean isDetail;

    public Comment_adapter(List<Comment> comment_list, boolean isDetail) {
        this.comment_list = comment_list;
        this.isDetail = isDetail;
    }

    @NonNull
    @Override
    //recyclerview에 레이아웃 지정정
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //Inflater : xml에 표기된 레이아웃들을 메모리에 객체화 하는 행동 수행
        if (isDetail) {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_detail, viewGroup, false);
            MyViewHolder myViewHolder = new MyViewHolder(holderView);
            return myViewHolder;
        } else {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment, viewGroup, false);
            MyViewHolder myViewHolder = new MyViewHolder(holderView);
            return myViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment comment = comment_list.get(position);
        String status = comment.getStatus();
        String rawComment = comment.getComment();
        holder.txt_comment.setText(rawComment);
        holder.txt_status.setText(status);

        if (status.equals("빡침")) {
            holder.txt_status.setTextColor(Color.parseColor("#D60620"));
        } else {
            holder.txt_status.setTextColor(Color.parseColor("#035096"));
        }
        Log.d(TAG, String.valueOf(position));
        Log.d(TAG, String.valueOf(getItemCount()));
    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }

    // Clear, Set comment_list
    public void clearComments() {
        this.comment_list.clear();
    }

    public void setComments(List<Comment> comment_list) {
        this.comment_list = comment_list;
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_comment;
        private TextView txt_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_status = itemView.findViewById(R.id.txt_status);
            txt_comment = itemView.findViewById(R.id.txt_comment);
        }
    }
}