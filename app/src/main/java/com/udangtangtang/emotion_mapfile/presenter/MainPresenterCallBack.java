package com.udangtangtang.emotion_mapfile.presenter;

import com.udangtangtang.emotion_mapfile.model.Comment;

import java.util.ArrayList;
import java.util.List;

public interface MainPresenterCallBack {
        void onSuccessGetCommentList(List<Comment> commentList);
        void onSuccessGetUserInfo();
        void onFail(Exception ex);
}
