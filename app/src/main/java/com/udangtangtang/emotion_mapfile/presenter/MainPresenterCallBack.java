package com.udangtangtang.emotion_mapfile.presenter;

import com.udangtangtang.emotion_mapfile.model.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface MainPresenterCallBack {
        void onSuccess(Optional<List<Comment>> commentsList, Optional<HashMap<String,String>> myComment);
}