package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.presenter.MainPresenterCallBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    private static City singletonCity;

    private City() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public static City getInstance() {
        if (singletonCity == null) {
            singletonCity = new City();
        }
        return singletonCity;
    }

    // HashMap을 기반으로 comment 속성만으로 이루어진 List 생성후 반환
    private List<Comment> createCommentList(Optional<HashMap<String, Object>> target) {
        long twoDay = 1000 * 60 * 60 * 24 * 2;
        Date nowDate = new Date();
        Long now_criteria = nowDate.getTime();
        Log.d(TAG, "현재시간" + String.valueOf(now_criteria));
        this.angryPeople = 0;
        this.happyPeople = 0;

        List<Comment> comments = new ArrayList<Comment>();

        // target이 비어있는 Optional 객체가 아닐 경우 commentList에 추가하는 작업을 진행
        target.ifPresent(t -> {
            for (String key : t.keySet()) {
                HashMap<String, String> userInfo = (HashMap) t.get(key);
                //userInfo 에서 타임을 String 값으로 받아오고 이를 정수형으로 변환
                //정수형으로 변환된 time 값을 현재 날짜의 2일 전 값과 비교
                Log.d(TAG, "userInfo size: " + userInfo.size());
                String temp = String.valueOf(userInfo.get("create_at"));
                long write_date = Long.parseLong(temp);
                Log.d(TAG, "userInfo size: " + write_date);

                //받아온 date가 현재 시간 기준 2일 안에 해당된다면
                //comment 인스턴스를 생성하여 list에 담음
                if (write_date > now_criteria - twoDay) {
                    Log.d(TAG, "정렬 진입");
                    Comment comment = new Comment();
                    comment.setComment(userInfo.get("comment"));
                    comment.setCreate_at(write_date);
                    comment.setDistrict("district");
                    comment.setStatus("status");
                    comments.add(comment);
                    if (userInfo.get("status").equals("기쁨"))
                        this.happyPeople += 1;
                    else
                        this.angryPeople += 1;
                }
            }
        });

        Comment[] comment_list = new Comment[comments.size()];
        for (int i = 0; i < comments.size(); i++) {
            comment_list[i] = comments.get(i);
        }

        sort_comment(comment_list, 0, comment_list.length - 1);

        comments.clear();

        comments.addAll(Arrays.asList(comment_list));

        return comments;
    }


    private void sort_comment(Comment[] comment_list, int left, int right) {
        int pl = left;
        int pr = right;
        Long mid = comment_list[(pl + pr)/2].getCreate_at();

        do{
            //작성된 날짜값을 기준으로 비교함 - 최신순 우선
            while (comment_list[pl].getCreate_at() > mid) pl++;
            while (comment_list[pr].getCreate_at() < mid) pr--;
            if(pl <= pr){
                swap(comment_list, pl++, pr--);
            }
        }while(pl <= pr);
        if(left < pr) sort_comment(comment_list, left, pr);
        if(left < right) sort_comment(comment_list, pl, right);
    }

    public void swap(Comment[] comment_list, int idx1, int idx2){
        Comment comment = comment_list[idx1];
        comment_list[idx1] = comment_list[idx2];
        comment_list[idx2] = comment;
    }


    // 매개변수를 통해 City 객체의 각 변수들 초기화 이후 FirebaseDatabase에서 현재 도시의 기온 및 user들의 comment를 획득
    public void setInitInfo(String myCity, String district, double latitude, double longitude, MainPresenterCallBack callBack,String id) {
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
                    Log.d(TAG, "cityInfo size : " + cityInfo.size());
                    temperature = Long.parseLong(String.valueOf(cityInfo.get("Temperature")));
                    // user의 comment 정보 획득
                    Optional<HashMap<String, Object>> users = Optional.ofNullable((HashMap) cityInfo.get("users"));
                    List<Comment> commentList = createCommentList(users);

                    // users가 비어있지 않으면 callback함수를 통해 결과 전송
                    users.ifPresent(u->callBack.onSuccess(commentList,Optional.ofNullable((HashMap<String,String>)u.get(id))));
                } catch (NullPointerException e) { // 현재 DB에 등록되어 있지 않은 도시의 경우!

                    // Temperature를 0으로 초기화
                    reference.child("Temperature").setValue(0);
                    // myCity 하위 데이터 획득
                    HashMap<String, Object> cityInfo = (HashMap) snapshot.getValue();

                    // 없던 도시였기 때문에 로컬 temperature 변수 0으로 설정
                    temperature = 0;

                    // 마찬가지로, 없던 도시였기 때문에 생성할 comment 리스트가 없음 -> Optional.empty()를 이용해 비어있는 Optional 객체 전달
                    List<Comment> commentList = createCommentList(Optional.empty());
                    callBack.onSuccess(commentList,Optional.empty());
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
        comment.setDistrict(district);
        comment.setLatitude(latitude);
        comment.setLongitude(longitude);

        try {
            DatabaseReference reference = firebaseDatabase.getReference(myCity);
            reference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // 회원이 이전에 등록한 comment의 존재 여부에 따라 null 값이 존재할 수 있기 때문에 Optional객체를 이용하여 생성
                    Optional<Comment> oldComment = Optional.ofNullable(snapshot.getValue(Comment.class));

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