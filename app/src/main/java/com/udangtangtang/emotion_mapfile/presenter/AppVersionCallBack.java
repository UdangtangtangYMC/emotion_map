package com.udangtangtang.emotion_mapfile.presenter;

import java.util.HashMap;

public interface AppVersionCallBack {
    public void onSuccess(HashMap<String, Object> version);
    public void onFail();
}
