package com.udangtangtang.emotion_mapfile.presenter;

import java.util.List;

public interface StatisticsCallBack {
    void onSuccess(List<Long> people, boolean myCityExist);

    void onFailed();
}
