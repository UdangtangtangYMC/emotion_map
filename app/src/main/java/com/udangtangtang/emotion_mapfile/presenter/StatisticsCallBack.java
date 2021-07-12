package com.udangtangtang.emotion_mapfile.presenter;

import java.util.HashMap;
import java.util.Optional;

public interface StatisticsCallBack {
    void onSuccess(Optional<HashMap> statusList);
    void onFailed();
}
