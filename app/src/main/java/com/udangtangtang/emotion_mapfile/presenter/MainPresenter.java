package com.udangtangtang.emotion_mapfile.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.Comment;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.Comment_list;
import com.udangtangtang.emotion_mapfile.view.MainActivity;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;
import com.udangtangtang.emotion_mapfile.view.SignInActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MainPresenter {

    private final String TAG = "MainPresenter";
    private final Context context;
    private PlusEmotion plusEmotion;
    private User user;
    private City city;
    private Comment_adapter comment_adapter;
    static final int PERMISSIONS_REQUEST = 0x0000001;
    private FusedLocationProviderClient loc;


    public MainPresenter(Context context, User user) {
        this.context = context;
        this.user = user;
        this.city = new City();
    }

    public String get_userName() {
        Log.d(TAG, user.getName());
        return user.getName();
    }

    public void add_emotion() {
        PlusEmotionPresenter plusEmotionPresenter = new PlusEmotionPresenter(context, city, user);
        plusEmotion = new PlusEmotion(context, plusEmotionPresenter);
        plusEmotion.callFunciton();
    }

    public void intent_CommentDetail() {
        if (comment_adapter != null) {
            Intent intent = new Intent(context, Comment_list.class);
            intent.putExtra("com.udangtangtang.emotion_mapfile.adapter.Comment_adapter", comment_adapter);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "멘트 목록 상세보기 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // 위치 권환 확인 메소드
    public void checkPermissions(MainActivity activity) {

        // 위치권한이 획득 되어있지 않은 경우
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "권한요청 실행");
            // 권한의 획득 사유를 표시해야 하는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION))
                Toast.makeText(context, "서비스 제공을 위해서 권한 설정이 필요합니다.", Toast.LENGTH_LONG).show();
            // 권한 요청
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
        getLocality(activity);
        Log.d(TAG, "getLocality 메서드 실행");

    }

    // 기기의 마지막 위치를 기반으로 현재 도시명을 획득하는 메소드
    @SuppressLint("MissingPermission")
    private void getLocality(MainActivity activity) {
        loc = LocationServices.getFusedLocationProviderClient(activity);
        loc.getLastLocation()
                .addOnSuccessListener(location -> {
                    Log.d(TAG, "getLastLocation -> onSuccess: " + "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());
                    try {
                        Geocoder geocoder = new Geocoder(context);
                        // 위도 경도를 매개변수로 Address 객체를 담은 리스트 생성
                        List<Address> fromLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if(fromLocation == null){
                            Log.d(TAG, "fromLocation == null");
                        }
                        // Address 객체에서 Locality 속성을 획득하고 위도, 경도, 도시명을 City 객체에 저장
                        String myCity = fromLocation.get(0).getAdminArea(); // 위치정보
                        city.setInitInfo(myCity,
                                location.getLatitude(),
                                location.getLongitude(),
                                new MainPresenterCallBack() {
                                    @Override
                                    public void onSuccessGetUserInfo() {
                                        activity.setInitInfo();
                                        Log.d(TAG, "내 위치 불러오기 성공");
                                    }
                                    @Override
                                    public void onSuccessGetCommentList(List<Comment> commentList) {
                                        // comment 상세보기에 쓰일 comment_adapter 생성
                                        comment_adapter = new Comment_adapter(commentList);
                                        // MainActivity 에 보일 ui 초기화
                                        List<String> comments = new ArrayList<String>();
                                        for(Comment comment : commentList){
                                            comments.add(comment.getComment());
                                        }
                                        activity.setInitMentList(comments);
                                        Log.d(TAG, "멘트 목록 불러오기 성공");
                                    }
                                    @Override
                                    public void onFail(Exception ex) {
                                        Log.d(TAG, "onFail: ");
                                    }
                                });
                    } catch (IOException e) {
                        Log.d(TAG, "onSuccess: failed");
                    }catch (NullPointerException e){
                        Log.d(TAG, "db에 존재 하지 않는 도시 입니다.");
                        Toast.makeText(context, "db에 존재 하지 않는 도시 입니다.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        Log.d(TAG, "위치정보를 가져오는데 실패하였습니다.");
                        Toast.makeText(context, "위치정보를 가져오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public String getUserCity() {
        return city.getMyCity();
    }

    public String getCityTemperature() {
        return String.valueOf(city.getTemperature());
    }

    public String getAngryPeople(){
        return String.valueOf(city.getAngryPeople());
    }

    public String getHappyPeople(){
        return String.valueOf(city.getHappyPeople());
    }

    public String getLoginMethod(){
        return user.getLogin_method();
    }

    public void logout_google(FirebaseAuth mAuth){
        mAuth.signOut();
        intent_SignInActivity();
        Log.d(TAG, "구글 로그아웃 성공");
    }

    public void logout_kakao(){
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                intent_SignInActivity();
                Log.d(TAG, "카카오 로그아웃 성공");
            }
        });
    }

    public void intent_SignInActivity(){
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }
}