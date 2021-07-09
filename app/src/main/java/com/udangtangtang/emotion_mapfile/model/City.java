package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenterCallBack;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class City {

    private final String TAG = "City";
    private final FirebaseDatabase firebaseDatabase;
    private double latitude;
    private double longitude;
    private String myCity;
    private String district;
    private long temperature;
    private long happyPeople;
    private long angryPeople;

    public City() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    // HashMap을 기반으로 comment 속성만으로 이루어진 List 생성후 반환
    private ArrayList<String> createCommentList(Optional<HashMap<String, Object>> target) {
        this.angryPeople = 0;
        this.happyPeople = 0;

        ArrayList<String> comments = new ArrayList<String>();

        // target이 비어있는 Optional 객체가 아닐 경우 commentList에 추가하는 작업을 진행
        target.ifPresent(t -> {
            for (String key : t.keySet()) {
                HashMap<String, String> userInfo = (HashMap) t.get(key);
                comments.add(userInfo.get("comment"));
                if (userInfo.get("status").equals("기쁨"))
                    this.happyPeople += 1;
                else
                    this.angryPeople += 1;
            }
        });
        comments.stream().forEach(c -> Log.d(TAG, "createCommentList: " + c));

        return comments;
    }

    private void sort_comment(List<Comment> comment_list) {
        for (Comment comment : comment_list) {

        }
    }


    // 매개변수를 통해 City 객체의 각 변수들 초기화 이후 FirebaseDatabase에서 현재 도시의 기온 및 user들의 comment를 획득
    public void setInitInfo(String myCity, String district, double latitude, double longitude, MainPresenterCallBack callBack) {
        this.myCity = myCity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.district = district;

        Log.d(TAG, "setInitInfo: " + myCity);
        // 현재 도시의 FirebaseDatabase 레퍼런스 획득
        DatabaseReference reference = firebaseDatabase.getReference(myCity);
        // valueListener 등록
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    // Temperature 값 획득 후 로컬 temperature 변수에 저장
                    HashMap<String, Object> cityInfo = (HashMap) snapshot.getValue();
                    temperature = Long.valueOf(String.valueOf(cityInfo.get("Temperature")));
                    // user의 comment 정보 획득
                    Optional<HashMap<String, Object>> users = Optional.ofNullable((HashMap) cityInfo.get("users"));
                    ArrayList<String> commentList = createCommentList(users);

                    // 완료될 경우 callBack.onSuccess 메소드 호출
                    callBack.onSuccess(commentList);
                } catch (NullPointerException e) { // 현재 DB에 등록되어 있지 않은 도시의 경우!

                    // Temperature를 0으로 초기화
                    reference.child("Temperature").setValue(0);
                    // myCity 하위 데이터 획득
                    HashMap<String, Object> cityInfo = (HashMap) snapshot.getValue();

                    // 없던 도시였기 때문에 로컬 temperature 변수 0으로 설정
                    temperature = 0;

                    // 마찬가지로, 없던 도시였기 때문에 생성할 comment 리스트가 없음 -> Optional.empty()를 이용해 비어있는 Optional 객체 전달
                    ArrayList<String> commentList = createCommentList(Optional.empty());
                    callBack.onSuccess(commentList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    public void insert_comment(Comment comment, String id) throws Exception {

        // comment의 위도, 경도, 구는 City 내의 정보들로 초기화.
        comment.setLatitude(latitude);
        comment.setLongitude(longitude);
        comment.setDistrict(district);

        try {
            DatabaseReference reference = firebaseDatabase.getReference(myCity);
            reference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // 회원이 이전에 등록한 comment의 존재 여부에 따라 null 값이 존재할 수 있기 때문에 Optional객체를 이용하여 생성
                    Optional<Comment> oldComment = Optional.ofNullable(snapshot.getValue(Comment.class));
                    Log.d(TAG, "onDataChange: " + (oldComment == null));

                    // oldComment가 비어있지 않은 경우 이전 상태와 현재 등록하려는 상태를 비교한후 db에 업데이트 이후, 도시의 온도를 변경
                    oldComment.ifPresent(old -> {
                        Log.d(TAG, "onDataChange: oldStatus" + old.getStatus());
                        Log.d(TAG, "onDataChange: newStatus" + comment.getStatus());
                        boolean statusChanged = false;
                        if (!old.getStatus().equals(comment.getStatus())) statusChanged = true;
                        reference.child("users").child(id).setValue(comment);
                        if (statusChanged) {
                            if (comment.getStatus().equals("빡침")) {
                                reference.child("Temperature").setValue(temperature + 1);
                            } else reference.child("Temperature").setValue(temperature - 1);
                        }
                    });

                    // 비어있는 경우 새로운 comment를 db에 등록
                    if(!oldComment.isPresent()){
                        reference.child("users").child(id).setValue(comment);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: ");
                }
            });

        } catch (Exception e) {
            Log.d(TAG, e.toString());
            throw new Exception();
        }
    }
}