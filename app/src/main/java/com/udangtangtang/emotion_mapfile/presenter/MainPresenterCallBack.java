package com.udangtangtang.emotion_mapfile.presenter;

import java.util.ArrayList;
import java.util.List;

public interface MainPresenterCallBack {
        void onSuccess(ArrayList<String> commentList);
        void onFail(Exception ex);
}
