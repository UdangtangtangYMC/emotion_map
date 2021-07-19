package com.udangtangtang.emotion_mapfile.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface StatisticsCallBack {
    void onSuccess(List<Long> people, boolean myCityExist);

    void onFailed();
}
