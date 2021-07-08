package com.udangtangtang.emotion_mapfile.presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.MainActivity;

public class SessionCallback implements ISessionCallback {
    private Activity activity;
    private User user;

    public SessionCallback(Activity activity){
        this.activity = activity;
    }
    //로그인에 성공한 상태
    @Override
    public void onSessionOpened() {
        requestMe();
    }
    //로그인에 실패한 상태
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        if(exception !=  null){
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }
        //세션 연결이 실패 했을때
        //로그인화면을 다시 불러옴
        activity.setContentView(R.layout.activity_mainaftersplash);
    }

    //사용자 정보 요청
    public void requestMe(){
        UserManagement.getInstance()
                .me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "세션이 닫혀 있음" + errorResult);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "사용자 정보 요청 실패" + errorResult);
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        //유저 정보 저장을 위한 객체 생성
                        user = new User();
                        Log.i("KAKAO_API", "사용자 아이디 : " + result.getId());
                        String id = String.valueOf(result.getId());
                        user.setName(id);
                        //user정보를 받아옴
                        UserPro kakaoAccount = result.getProperties();
                        if (kakaoAccount != null) {
                            String email = kakaoAccount.getEmail();
                            if(email != null){
                                user.setID(id);
                            }else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE){

                            }
                            Profile profile = kakaoAccount.getProfile();

                            if (profile == null) {
                                Log.d("KAKAO_API", "onSuccess:profile null");
                            } else {
                                Log.d("KAKAO_API", "onSuccess:getProfileImageUrl" + profile.getProfileImageUrl());
                                Log.d("KAKAO_API", "onSuccess:getNickname" + profile.getNickname());
                            }
                            if (email != null) {
                                Log.d("KAKAO_API", "onSuccess:email" + email);
                            }
                            //프로필
                            Profile _profile = kakaoAccount.getProfile();

                            if (_profile != null) {
                                Log.d("KAKAO_API", "nickname: " + _profile.getNickname());
                                Log.d("KAKAO_API", "profile image: " + _profile.getProfileImageUrl());
                                Log.d("KAKAO_API", "thumbnail image: " + _profile.getThumbnailImageUrl());
                            } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 프로필 정보 휙득 가능
                            } else {
                                // 프로필 획득 불가
                            }
                        } else {
                            Log.i("KAKAO_API", "onSuccess : kakaoAccount null");
                        }
                        intent_MainActivity();
                    }
               });
    }

    private void intent_MainActivity(){
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("user",this.user);
        activity.startActivity(intent);
        activity.finish();
    }
}
