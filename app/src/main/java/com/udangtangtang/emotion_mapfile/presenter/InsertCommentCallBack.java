package com.udangtangtang.emotion_mapfile.presenter;

import java.util.Optional;

public interface InsertCommentCallBack {
    void onSuccess(Optional<Boolean> statusChanged, String status);
}
