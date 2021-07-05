package com.udangtangtang.emotion_mapfile.presenter;

import java.util.List;

public interface CommentListCallBack {
        void onSuccess(List<String> comment_list);
        void onFail(Exception ex);
}
