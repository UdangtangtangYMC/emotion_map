package com.udangtangtang.emotion_mapfile.presenter;

import java.util.List;

public interface MainPresenterCallBack {
        void onSuccess(List<String> commentList);
        void onFail(Exception ex);
}
