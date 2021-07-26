package com.udangtangtang.emotion_mapfile.presenter;

import com.udangtangtang.emotion_mapfile.model.Comment;

import java.util.List;
import java.util.Optional;

/**
 * 다른 유저들의 Comment를 읽어들여 리스트 형태로 변환하는 과정에서
 * FirebaseDatabase와의 통신이 성공적으로 완료되었음을 알려주는 콜백 인터페이스
 */
public interface CommentListCallBack {
    void onSuccess(Optional<List<Comment>> commentList);
    void onFailed();
}