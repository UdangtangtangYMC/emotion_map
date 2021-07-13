package com.udangtangtang.emotion_mapfile.presenter;

import java.util.HashMap;
import java.util.Optional;

public interface SetChartCallBack {
    public void SuccessGetStatus(Optional<HashMap> statusList);
    public void OnFailGetStatus();
}
