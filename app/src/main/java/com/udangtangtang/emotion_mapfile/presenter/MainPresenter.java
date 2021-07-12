package com.udangtangtang.emotion_mapfile.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.udangtangtang.emotion_mapfile.view.NationalStatistics;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;
import com.udangtangtang.emotion_mapfile.view.SignInActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MainPresenter {

    private final String TAG = "MainPresenter";
    private final Context context;
    private final User user;
    private final City city;
    private Comment_adapter comment_adapter;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    public MainPresenter(Context context, User user) {
        this.context = context;
        this.user = user;
        this.city = City.getInstance();
    }

    public String get_userName() {
        Log.d(TAG, user.getName());
        return user.getName();
    }

    public void add_emotion() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            PlusEmotionPresenter plusEmotionPresenter = new PlusEmotionPresenter(context, city, user);
            PlusEmotion plusEmotion = new PlusEmotion(context, plusEmotionPresenter);
            plusEmotion.callFunciton();
        } else {
            Toast.makeText(context, "위치 권한 사용에 동의하여야 합니다.", Toast.LENGTH_SHORT).show();
        }
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

    public void intent_SignInActivity() {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    public void intent_NationalStatistics() {
        Intent intent = new Intent(context, NationalStatistics.class);
        context.startActivity(intent);
    }


    // 위치 권환 확인 메소드
    public void checkPermissions(MainActivity activity) {

        Log.d(TAG, "checkPermissions: " + ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION));
        // 위치권한이 획득 되어있지 않은 경우
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "권한요청 실행");
            Log.d(TAG, "checkPermissions: if entered");
            // 권한의 획득 사유를 표시해야 하는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION))
                // 토스트를 생성해 적절한 안내 메시지 제공
                Toast.makeText(context, "서비스 제공을 위해서 권한 설정이 필요합니다.", Toast.LENGTH_LONG).show();

            Log.d(TAG, "checkPermissions: entered");
            // 권한 요청
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
            Log.d(TAG, "checkPermissions: finished");
        } else {
            Log.d(TAG, "checkPermissions: else entered");
            // 권한이 이미 승인되어있다면 getLocality() 호출
            getLocality(activity);
        }
        Log.d(TAG, "getLocality 메서드 실행");

    }

    @SuppressLint("MissingPermission")
    public void getLocality(MainActivity activity) {
        Log.d(TAG, "getLocality entered: ");
        FusedLocationProviderClient locProvider = LocationServices.getFusedLocationProviderClient(activity);
        updateLocation(locProvider);
        locProvider.getLastLocation()
                .addOnSuccessListener(location -> {
                    Log.d(TAG, "getLastLocation -> onSuccess: " + "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());
                    try {
                        Geocoder geocoder = new Geocoder(context);
                        // 위도 경도를 매개변수로 Address 객체를 담은 리스트 생성
                        Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);

                        // Address 객체에서 Locality 속성을 획득
                        // subLocality는 구로 고정 되어있음, 속해있는 도가 없는 경우 -> getAdminArea를 통해 해당 시 접근, 도가 있는 존재하는 경우 -> getLocality를 통해 시 획득
                        String locality = address.getLocality();

                        Log.d(TAG, "getLocality: " + address.getAdminArea() + address.getLocality() + address.getSubAdminArea() + address.getSubLocality());

                        // MainPresenterCallBack 생성
                        MainPresenterCallBack mainPresenterCallBack = createCallBack(activity);

                        // 현재 위치가 특별시, 광역시, 특별자치도 등 도에 속해있지 않은 경우
                        if (locality == null) {
                            locality = address.getAdminArea();
                        }
                        // 위도, 경도, 도시명을 City 객체에 저장
                        city.setInitInfo(locality,
                                address.getSubLocality(),
                                location.getLatitude(),
                                location.getLongitude(),
                                mainPresenterCallBack,
                                user.getID());
                        // 위도 경도를 매개변수로 Address 객체를 담은 리스트 생성
                    } catch (IOException e) {
                        Log.d(TAG, "onSuccess: failed");
                    } catch (NullPointerException e) {
                        Log.d(TAG, "db에 존재 하지 않는 도시 입니다.");
                        Toast.makeText(context, "db에 존재 하지 않는 도시 입니다.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
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

    public String getAngryPeople() {
        return String.valueOf(city.getAngryPeople());
    }

    public String getHappyPeople() {
        return String.valueOf(city.getHappyPeople());
    }

    public MainPresenterCallBack createCallBack(MainActivity activity) {
        return new MainPresenterCallBack() {
            @Override
            public void onSuccess(Optional<List<Comment>> commentList, Optional<HashMap<String, String>> myComment) {
                Toast.makeText(activity.getApplicationContext(), "새로고침 하였습니다.", Toast.LENGTH_SHORT).show();
                commentList.ifPresent(c -> {
                    // comment 상세보기에 쓰일 comment_adapter 생성
                    comment_adapter = new Comment_adapter(c);
                    // MainActivity 에 보일 ui 초기화
                    List<String> comments = new ArrayList<>();
                    for (Comment comment : c) {
                        comments.add(comment.getComment());
                    }

                    // 자신의 이전 comment가 존재하는 경우
                    myComment.ifPresent(mc -> {
                        Log.d(TAG, "onSuccess: myComment"+mc.get("status")+" "+mc.get("comment"));
                        // 타인의 comments, 자신의 이전 상태, 자신의 이전 comment를 매개변수로 전달
                        activity.setInitInfo(Optional.of(comments), Optional.ofNullable(mc.get("status")), Optional.ofNullable(mc.get("comment")));
                    });
                    // 자신의 이전 comment가 없는 경우
                    if (!myComment.isPresent()) {
                        // 타인의 comments와 Optional.empty를 매개변수로 전달
                        activity.setInitInfo(Optional.of(comments), Optional.empty(), Optional.empty());
                    }
                    activity.blink();
                });
                if (!commentList.isPresent()) {
                    activity.setInitInfo(Optional.empty(),Optional.empty(),Optional.empty());
                }
            }
        };
    }

    public String getLoginMethod() {
        return user.getLogin_method();
    }

    public void logout_google(FirebaseAuth mAuth) {
        mAuth.signOut();
        intent_SignInActivity();
        Log.d(TAG, "구글 로그아웃 성공");
    }

    public void logout_kakao() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                intent_SignInActivity();
                Log.d(TAG, "카카오 로그아웃 성공");
            }
        });
    }

    // 아직 수정이 필요한 메소드
    @SuppressLint("MissingPermission")
    private void updateLocation(FusedLocationProviderClient loc) {
        Log.d(TAG, "updateLocation: ");
        LocationRequest locationRequest = LocationRequest.create().
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        loc.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: " + lastLocation.getLatitude() + lastLocation.getLongitude());
            }
        }, Looper.getMainLooper());

    }

    public void intent_nationalStatistics() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(context, NationalStatistics.class);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "GPS 사용 권환 획득 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // Clear, Set comment_list
    public void clearComments() {
        this.comment_adapter.clearComments();
    }

    public void setComments(ArrayList<Comment> comment_list) {
        this.comment_adapter.setComments(comment_list);
    }

    // notifyDataSetChanged()
    public void notifyDataChanged() {
        this.comment_adapter.notifyDataSetChanged();
    }
}