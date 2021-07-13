package com.udangtangtang.emotion_mapfile.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.google.android.gms.common.api.Api;
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
import com.udangtangtang.emotion_mapfile.model.CityStatus;
import com.udangtangtang.emotion_mapfile.model.Comment;
import com.udangtangtang.emotion_mapfile.model.User;
import com.udangtangtang.emotion_mapfile.view.Comment_list;
import com.udangtangtang.emotion_mapfile.view.MainActivity;
import com.udangtangtang.emotion_mapfile.view.NationalStatistics;
import com.udangtangtang.emotion_mapfile.view.PlusEmotion;
import com.udangtangtang.emotion_mapfile.view.SignInActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import kotlin.reflect.KMutableProperty2;

public class MainPresenter {

    private final String TAG = "MainPresenter";
    private final Context context;
    private final User user;
    private final City city;
    private Comment_adapter comment_adapter;
    static final int PERMISSIONS_REQUEST = 0x0000001;
    private List<CityStatus> cityStatusesList = new ArrayList<CityStatus>();
    private MainActivity activity;

    public MainPresenter(Context context, User user, MainActivity activity) {
        this.context = context;
        this.user = user;
        this.city = City.getInstance();
        this.activity = activity;
    }

    public String get_userName() {
        Log.d(TAG, user.getName());
        return user.getName();
    }

    public void add_emotion() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            PlusEmotionPresenter plusEmotionPresenter = new PlusEmotionPresenter(context, city, user);
            PlusEmotion plusEmotion = new PlusEmotion(context, plusEmotionPresenter);
            plusEmotion.callFunciton(addEmotionCallback());
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
        NationalStatisticsPresenter nationalStatisticsPresenter = new NationalStatisticsPresenter(this.cityStatusesList);
        Log.d(TAG, "cityStatusList size" + String.valueOf(cityStatusesList.size()));
        intent.putExtra("nationalStatisticsPresenter", nationalStatisticsPresenter);
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
                    try {
                        Log.d(TAG, "getLastLocation -> onSuccess: " + "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());
                        Geocoder geocoder = new Geocoder(context);
                        // 위도 경도를 매개변수로 Address 객체를 담은 리스트 생성
                        Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);

                        // Address 객체에서 Locality 속성을 획득
                        // subLocality는 구로 고정 되어있음, 속해있는 도가 없는 경우 -> getAdminArea를 통해 해당 시 접근, 도가 있는 존재하는 경우 -> getLocality를 통해 시 획득
                        String locality = address.getLocality();

                        Log.d(TAG, "getLocality: " + address.getAdminArea() + address.getLocality() + address.getSubAdminArea() + address.getSubLocality());

                        // 현재 위치가 특별시, 광역시, 특별자치도 등 도에 속해있지 않은 경우
                        if (locality == null) {
                            locality = address.getAdminArea();
                        }
                        // 위도, 경도, 도시명을 City 객체에 저장
                        city.setInitInfo(locality,
                                address.getSubLocality(),
                                location.getLatitude(),
                                location.getLongitude());
                        setInitInfo(activity);
                        Log.d(TAG, "getLocality: currentMill : " + System.currentTimeMillis() + " city.Mycity : " + city.getMyCity());
                        // 위도 경도를 매개변수로 Address 객체를 담은 리스트 생성
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


    private void setChart(HashMap statuses) {
        CityStatus[] cityStatuses = new CityStatus[statuses.size()];
        this.cityStatusesList.clear();
        int index = 0;
        //status로 부터 값을 꺼내와
        for(Object key : statuses.keySet()){
            CityStatus cityStatus = new CityStatus();
            String cityName = key.toString();
            cityStatus.setName(cityName);

            HashMap<String, String> status = (HashMap) statuses.get(key);
            String angry_temp = String.valueOf(status.get("angry_people"));
            String happy_temp = String.valueOf(status.get("happy_people"));
            int angry_people = Integer.parseInt(angry_temp);
            int happy_people = Integer.parseInt(happy_temp);
            int total = angry_people + happy_people;
            double ratio = (double) angry_people / total * 100;
            Log.d(TAG, cityName + " " + angry_temp +" "+ happy_temp + ratio);

            cityStatus.setAngry_count(angry_people);
            cityStatus.setHappy_count(happy_people);
            cityStatus.setTotal(total);
            cityStatus.setRatio((int) ratio);
            cityStatuses[index] = cityStatus;
            index++;
        }
        Log.d(TAG, String.valueOf(cityStatuses.length));
        sortByProportion(cityStatuses, 0, cityStatuses.length-1);

        this.cityStatusesList.addAll(Arrays.asList(cityStatuses));


        //메인화면에는 표의 data가 5개만 표시되므로
        Log.d(TAG, "cityStatusesList.size() = "+String.valueOf(cityStatusesList.size()));
        if(cityStatusesList.size() < 5){
            for(int i=0;i<cityStatusesList.size();i++){
                activity.setChart(cityStatusesList.get(i).getName(), cityStatusesList.get(i).getAngry_count(),
                        cityStatusesList.get(i).getHappy_count(), cityStatusesList.get(i).getTotal(), i);
            }
        }else{
            for (int i = 0; i < 5; i++) {
                //정렬된 리스트인 this.List<CityStatus>에서 부터
                //각 index에 담긴 인스턴스에 설정된 속성 값들을 바탕으로
                //MainActivity의 ()메서드 cityStatusesList[0].getName..등을 매개변수로 넘김
                activity.setChart(cityStatusesList.get(i).getName(), cityStatusesList.get(i).getAngry_count(),
                        cityStatusesList.get(i).getHappy_count(), cityStatusesList.get(i).getTotal(), i);
            }
        }
    }

    private void sortByProportion(CityStatus[] cityStatuses, int left, int right) {
        //CityStatus[]를 sort_quick을 통해 정렬
        int pl = left;
        int pr = right;
        int mid = cityStatuses[(pl + pr) / 2].getRatio();

        do {
            Log.d(TAG, String.valueOf(pl) + String.valueOf(pr));
            while (cityStatuses[pl].getRatio() > mid) pl++;
            while (cityStatuses[pr].getRatio() < mid) pr--;
            if (pl <= pr) {
                swap(cityStatuses, pl++, pr--);
            }
        } while (pl <= pr);
        if (left < pr) sortByProportion(cityStatuses, left, pr);
        if (left < right) sortByProportion(cityStatuses, pl, right);
    }

    private void swap(CityStatus[] cityStatuses, int idx1, int idx2) {
        CityStatus cityStatus = cityStatuses[idx1];
        cityStatuses[idx1] = cityStatuses[idx2];
        cityStatuses[idx2] = cityStatus;
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

    private void setInitInfo(MainActivity activity) {
        setCommentList(activity);
        setStatistics(activity);
    }

    private CommentListCallBack createCommentListCallBack(MainActivity activity) {
        return new CommentListCallBack() {
            @Override
            public void onSuccess(Optional<List<Comment>> commentList) {
                Log.d(TAG, "onSuccess: ");
                commentList.ifPresent(c -> {
                    // comment 상세보기에 쓰일 comment_adapter 생성
                    comment_adapter = new Comment_adapter(c);

                    // MainActivity 에 보일 ui 초기화
                    List<String> comments = new ArrayList<>();

                    for (Comment comment : c) {
                        comments.add(comment.getComment());
                    }

                    activity.setComments(Optional.of(comments));

                    city.addMyCommentListener(user.getID(), createMyCommentCallBack(activity));
                });
            }

            @Override
            public void onFailed() {
                Log.d(TAG, "onFailed: ");
                activity.setComments(Optional.empty());
                city.addMyCommentListener(user.getID(), createMyCommentCallBack(activity));
            }
        };
    }

    private void setCommentList(MainActivity activity) {
        city.getCommentList(createCommentListCallBack(activity), user.getID());
    }

    private void setStatistics(MainActivity activity) {
        city.getStatistics(createStatisticsCallBack(activity), setChartCallBack());
    }

    private StatisticsCallBack createStatisticsCallBack(MainActivity activity) {
        return new StatisticsCallBack() {
            @Override
            public void onSuccess(Optional<HashMap> statusList, boolean myCityExist) {
                statusList.ifPresent(status -> {
                    Long temperature = Long.parseLong("0");
                    Long happyPeople = Long.parseLong("0");
                    Long angryPeople = Long.parseLong("0");
                    if(myCityExist) {
                        HashMap<String, Long> myCityStatus = (HashMap) status.get(city.getMyCity());
                        temperature = myCityStatus.get("Temperature");
                        happyPeople = myCityStatus.get("happy_people");
                        angryPeople = myCityStatus.get("angry_people");
                    }
                    activity.setCityStats(String.valueOf(temperature), String.valueOf(happyPeople), String.valueOf(angryPeople));

                    // write your code here, SungMin or wherever you want.
                });
            }

            @Override
            public void onFailed() {
                activity.setCityStats("0", "0", "0");
            }
        };
    }

    private MyCommentCallBack createMyCommentCallBack(MainActivity activity) {
        return new MyCommentCallBack() {
            @Override
            public void onSuccess(String status, String comment) {
                activity.setMyRecentComment(Optional.ofNullable(status), Optional.ofNullable(comment));
            }

            @Override
            public void onFailed() {
                activity.setMyRecentComment(Optional.empty(), Optional.empty());
            }
        };
    }

    private SetChartCallBack setChartCallBack(){
        return new SetChartCallBack() {
            @Override
            public void SuccessGetStatus(Optional<HashMap> statusList) {
                //statusList를 불러오기 성공한 상태임으로 setChart실행
                statusList.ifPresent(u -> setChart(u));
            }

            @Override
            public void OnFailGetStatus() {
                Toast.makeText(context, "표 불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private AddEmotionCallback addEmotionCallback(){
        return new AddEmotionCallback() {
            @Override
            public void onSuccess() {
                activity.refresh();
            }
        };
    }
}
