package com.udangtangtang.emotion_mapfile.presenter;

import java.util.ArrayList;
import java.util.List;

public interface MainPresenterCallBack {
        void onSuccessGetCommentList(ArrayList<String> commentList);
        void onSuccessGetUserInfo();
        void onFail(Exception ex);
}
