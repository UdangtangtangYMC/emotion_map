package com.udangtangtang.emotion_mapfile.presenter;

import com.udangtangtang.emotion_mapfile.model.Comment;

import java.util.List;
import java.util.Optional;

public interface InsertCommentCallBack {
    void onUploadSuccess(Optional<Boolean> statusChanged, String status);

    void onUpdateSuccess(List<Comment> comments);
}
