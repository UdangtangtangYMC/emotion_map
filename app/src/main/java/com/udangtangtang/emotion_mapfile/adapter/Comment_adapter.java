package com.udangtangtang.emotion_mapfile.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Comment_adapter extends RecyclerView.Adapter<Comment_adapter.MyViewHolder> implements Serializable {
    private final String TAG = "Comment_adapter";
    private List<String> comment_list = new ArrayList<String>();

    protected class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_comment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_comment = itemView.findViewById(R.id.txt_comment);
        }
    }

    public Comment_adapter(List<String> comment_list){
        this.comment_list = comment_list;
    }

    @NonNull
    @Override
    //recyclerview에 레이아웃 지정정
   public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //Inflater : xml에 표기된 레이아웃들을 메모리에 객체화 하는 행동 수행
        View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(holderView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String comment = comment_list.get(position);
        holder.txt_comment.setText(comment);
        Log.d(TAG, String.valueOf(position));
        Log.d(TAG, String.valueOf(getItemCount()));
    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }
}
