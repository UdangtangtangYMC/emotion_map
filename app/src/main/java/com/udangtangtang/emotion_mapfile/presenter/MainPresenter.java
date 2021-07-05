package com.udangtangtang.emotion_mapfile.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.udangtangtang.emotion_mapfile.adapter.Comment_adapter;
import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.Comment_list;
import com.udangtangtang.emotion_mapfile.view.MainActivity;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;

import org.w3c.dom.Comment;

import java.io.IOException;
import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import lombok.Synchronized;

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

//    public void getCommentList() {
//        Log.d(TAG, "getCommentList: called");
//        city.getCommentList("Anyang");
//        Log.d(TAG, "getCommentList: finished");
//    }

    public void add_emotion() {
        plusEmotion = new PlusEmotion(context);
        plusEmotion.callFunciton();
    }


    public void insert_CommentList(RecyclerView comment_view) {
        //db로 부터 데이터를 받아오고 입력한 city를 기반으로한 data 배열 생성
        //커멘트 어댑터 생성 및 데이터 recyclerview에 들어갈 data set
        city.getCommentList("Anyang", new CommentListCallBack() {
            @Override
            public void onSuccess(List<String> comment_list) {
                comment_adapter = new Comment_adapter(comment_list);
                comment_view.setAdapter(comment_adapter);
            }

            @Override
            public void onFail(Exception ex) {
                Log.d(TAG, "onFail: ");
            }
        });
    }

    public void intent_CommentDetail() {
        Intent intent = new Intent(context, Comment_list.class);
        intent.putExtra("com.udangtangtang.emotion_mapfile.adapter.Comment_adapter", comment_adapter);
        context.startActivity(intent);
    }

    public void getLastKnownLocation() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
    }


    public void setLatitude(Double latitude) {
        city.setLatitude(latitude);
    }

    public void setLongitude(Double longitude) {
        city.setLongitude(longitude);
    }

    public Double getLatitude() {
        return city.getLatitude();
    }

    public Double getLongitude() {
        return city.getLongitude();
    }

    public City getCity() {
        return this.city;
    }


    // 위치 권환 확인 메소드
    public void checkPermissions(Context context, Activity activity) {

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
        loc = LocationServices.getFusedLocationProviderClient(activity);
        loc.getLastLocation()
                .addOnSuccessListener(location -> {
                    Log.d(TAG, "getLastLocation -> onSuccess: " + "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());
                    try {
                        Geocoder geocoder = new Geocoder(context);
                        List<Address> fromLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
                        city.setLatitude(location.getLatitude());
                        city.setLongitude(location.getLongitude());
                        city.setMyCity(fromLocation.get(0).getLocality());
                    } catch (IOException e) {
                        Log.d(TAG, "onSuccess: failed");
                    }
                });
    }
}