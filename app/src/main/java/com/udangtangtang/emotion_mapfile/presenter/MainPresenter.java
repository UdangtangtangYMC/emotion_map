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
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.Comment_list;
import com.udangtangtang.emotion_mapfile.view.MainActivity;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;

import java.io.IOException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainPresenter {

    private final String TAG = "MainPresenter";
    private final Context context;
    private PlusEmotion plusEmotion;
    private User user;
    private City city;
    private Comment_adapter comment_adapter;
    static final int PERMISSIONS_REQUEST = 0x0000001;
    private FusedLocationProviderClient loc;

    public MainPresenter(Context context) {
        this.context = context;

        this.user = new User();
        this.city = new City();
    }

    public void add_emotion() {
        plusEmotion = new PlusEmotion(context);
        plusEmotion.callFunciton();
    }

    public void intent_CommentDetail() {
        Intent intent = new Intent(context, Comment_list.class);
        intent.putExtra("com.udangtangtang.emotion_mapfile.adapter.Comment_adapter", comment_adapter);
        context.startActivity(intent);
    }

    // 위치 권환 확인 메소드
    public void checkPermissions(MainActivity activity) {

        // 위치권한이 획득 되어있지 않은 경우
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // 권한의 획득 사유를 표시해야 하는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION))
                Toast.makeText(context, "서비스 제공을 위해서 권한 설정이 필요합니다.", Toast.LENGTH_LONG).show();
            // 권한 요청
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
        getLocality(activity);

    }

    @SuppressLint("MissingPermission")
    private void getLocality(MainActivity activity) {
        loc = LocationServices.getFusedLocationProviderClient(activity);
        loc.getLastLocation()
                .addOnSuccessListener(location -> {
                    Log.d(TAG, "getLastLocation -> onSuccess: " + "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());
                    try {
                        Geocoder geocoder = new Geocoder(context);
                        List<Address> fromLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        city.setInitInfo(fromLocation.get(0).getLocality(),
                                location.getLatitude(),
                                location.getLongitude(),
                                new MainPresenterCallBack() {
                                    @Override
                                    public void onSuccess(List<String> commentList) {
                                        comment_adapter = new Comment_adapter(commentList);
                                        activity.setInitInfo(comment_adapter);
                                    }

                                    @Override
                                    public void onFail(Exception ex) {
                                        Log.d(TAG, "onFail: ");
                                    }
                                });
                    } catch (IOException e) {
                        Log.d(TAG, "onSuccess: failed");
                    }
                });
    }

    public String getUserCity(){
        return city.getMyCity();
    }

    public String getCityTemperature(){
        return String.valueOf(city.getTemperature());
    }
}