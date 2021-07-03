package com.udangtangtang.emotion_mapfile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.udangtangtang.emotion_mapfile.R;

import java.util.ArrayList;

public class Comment_adapter extends RecyclerView.Adapter<Comment_adapter.MyViewHolder>{
    ArrayList<String> comment_list;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_comment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_comment = itemView.findViewById(R.id.txt_comment);
        }
    }

    public Comment_adapter(ArrayList<String> comment_list){
        this.comment_list = comment_list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(holderView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String comment = comment_list.get(position);
        holder.txt_comment.setText(comment);
    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }
}
