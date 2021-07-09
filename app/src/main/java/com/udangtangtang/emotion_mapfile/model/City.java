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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class City{

    private final String TAG = "City";
    private final FirebaseDatabase firebaseDatabase;
    private double latitude;
    private double longitude;
    private String myCity;
    private long temperature;
    private long happyPeople;
    private long angryPeople;

    public City() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    // HashMap을 기반으로 comment 속성만으로 이루어진 List 생성후 반환
    private List<Comment> createCommentList(HashMap<String, Object> target) {
        long twoDay = 1000 * 60 * 60 * 24;
        Date nowDate = new Date();
        Long now_criteria = Long.valueOf(nowDate.getYear() + nowDate.getMonth() + nowDate.getDay());
        this.angryPeople = 0;
        this.happyPeople = 0;

        List<Comment> comments = new ArrayList<Comment>();

        for (String key : target.keySet()) {
            HashMap<String, String> userInfo = (HashMap) target.get(key);
            //userInfo 에서 타임을 String 값으로 받아오고 이를 정수형으로 변환
            //정수형으로 변환된 time 값을 현재 날짜의 2일 전 값과 비교
            Long write_date = Long.parseLong(userInfo.get("create_At"));
            //받아온 date가 현재 시간 기준 2일 안에 해당된다면
            //comment 인스턴스를 생성하여 list에 담음
            if(write_date > now_criteria - twoDay){
                Comment comment = new Comment();
                comment.setComment(userInfo.get("comment"));
                comment.setCreate_at(write_date);
                comment.setDistrict("district");
                comment.setStatus("status");
                if(userInfo.get("status").equals("좋음"))
                    this.happyPeople+=1;
                else
                    this.angryPeople+=1;
                comments.add(comment);
            }
        }

        Comment[] comment_list = new Comment[comments.size()];
        for(int i=0;i<comments.size();i++){
            comment_list[i] = comments.get(i);
        }

        sort_comment(comment_list, 0, comment_list.length-1);

        comments.clear();

        for(int i=0;i<comments.size();i++){
                comments.add(comment_list[i]);
        }

        comments.stream().forEach(c -> Log.d(TAG, "createCommentList: " + c));
        return comments;
    }

    private void sort_comment(Comment[] comment_list, int left, int right) {
        int pl = left;
        int pr = right;
        Long mid = comment_list[(pl + pr)].getCreate_at();

        do{
            //작성된 날짜값을 기준으로 비교함 - 최신순 우선
            while (comment_list[pl].getCreate_at() > mid) pl++;
            while (comment_list[pr].getCreate_at() < mid) pr--;
            if(pl <= pr){
                swap(comment_list, pl++, pr--);
            }
        }while(pl <= pr);
    }

    public void swap(Comment[] comment_list, int idx1, int idx2){
        Comment comment = comment_list[idx1];
        comment_list[idx1] = comment_list[idx2];
        comment_list[idx2] = comment_list[idx1];
    }


    // 매개변수를 통해 City 객체의 각 변수들 초기화 이후 FirebaseDatabase에서 현재 도시의 기온 및 user들의 comment를 획득
    public void setInitInfo(String myCity, double latitude, double longitude, MainPresenterCallBack callBack) throws Exception{
        this.myCity = myCity;
        this.latitude = latitude;
        this.longitude = longitude;
        DatabaseReference reference = null;

        if (myCity != null){
            callBack.onSuccessGetUserInfo();
        }else{
            throw new Exception();
        }

        // 현재 도시의 FirebaseDatabase 레퍼런스 획득
        reference = firebaseDatabase.getReference(myCity);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: dgdgdg");

                // Temperature 값 획득
                HashMap<String, Object> cityInfo = (HashMap) snapshot.getValue();
                temperature = (long) cityInfo.get("Temperature");

                // user의 comment 정보 획득
                HashMap<String, Object> users = (HashMap) cityInfo.get("users");
                List<Comment> commentList = createCommentList(users);
                callBack.onSuccessGetCommentList(commentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    public void insert_comment(Comment comment, String city, String id) throws Exception {
        try {
            DatabaseReference reference = firebaseDatabase.getReference(city);
            reference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Comment oldComment = snapshot.getValue(Comment.class);
                    boolean statusChanged = false;
                    Log.d(TAG, "onDataChange: oldStatus"+oldComment.getStatus());
                    Log.d(TAG, "onDataChange: newStatus"+comment.getStatus());
                    if (!oldComment.getStatus().equals(comment.getStatus())) statusChanged = true;
                    reference.child("users").child(id).setValue(comment);
                    if (statusChanged) {
                        if (comment.getStatus().equals("빡침")) {
                            reference.child("Temperature").setValue(temperature + 1);
                        } else reference.child("Temperature").setValue(temperature - 1);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            Log.d(TAG, e.toString());
            throw new Exception();
        }
    }
}