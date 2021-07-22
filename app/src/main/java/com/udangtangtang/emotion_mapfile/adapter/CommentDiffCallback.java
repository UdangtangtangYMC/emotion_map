package com.udangtangtang.emotion_mapfile.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.udangtangtang.emotion_mapfile.model.Comment;

import org.jetbrains.annotations.NotNull;

public class CommentDiffCallback extends DiffUtil.ItemCallback<Comment> {

    private static CommentDiffCallback singletonDiff;

    private CommentDiffCallback() {

    }

    public static CommentDiffCallback getInstance(){
        if (singletonDiff == null) {
            singletonDiff = new CommentDiffCallback();
        }
        return singletonDiff;
    }

    @Override
    public boolean areItemsTheSame(@NonNull @NotNull Comment oldItem, @NonNull @NotNull Comment newItem) {
        return oldItem.getStatus().equals(newItem.getStatus());
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull Comment oldItem, @NonNull @NotNull Comment newItem) {
        return oldItem.getComment().equals(newItem.getComment());
    }
}
