package com.udangtangtang.emotion_mapfile.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.model.Comment;

import java.io.Serializable;

public class CommentAdapter extends ListAdapter<Comment, CommentAdapter.CommentViewHolder> implements Serializable {
    private static CommentAdapter singletonAdapter;
    private final String TAG = "Comment_adapter";
    private boolean isDetail;

    private CommentAdapter(@NonNull DiffUtil.ItemCallback<Comment> diffCallback, boolean isDetail) {
        super(diffCallback);
        this.isDetail = isDetail;
    }

    public static CommentAdapter getInstance(@NonNull DiffUtil.ItemCallback<Comment> diffCallback, boolean isDetail){
        if (singletonAdapter == null) {
            singletonAdapter = new CommentAdapter(diffCallback, isDetail);
        }
        return singletonAdapter;
    }


    @NonNull
    @Override
    //recyclerview에 레이아웃 지정정
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //Inflater : xml에 표기된 레이아웃들을 메모리에 객체화 하는 행동 수행
        if (isDetail) {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_detail, viewGroup, false);
            return new CommentViewHolder(holderView);
        } else {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment, viewGroup, false);
            return new CommentViewHolder(holderView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    protected class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_comment;
        private TextView txt_status;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_status = itemView.findViewById(R.id.txt_status);
            txt_comment = itemView.findViewById(R.id.txt_comment);
        }

        private void bind(Comment comment) {
            txt_status.setText(comment.getStatus());
            txt_comment.setText(comment.getComment());

            if (comment.getStatus().equals("빡침")) {
                txt_status.setTextColor(Color.parseColor("#D60620"));
            } else {
                txt_status.setTextColor(Color.parseColor("#035096"));
            }
        }
    }
}