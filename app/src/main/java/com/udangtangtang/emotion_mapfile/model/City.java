package com.udangtangtang.emotion_mapfile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udangtangtang.emotion_mapfile.presenter.CommentListCallBack;
import com.udangtangtang.emotion_mapfile.presenter.InsertCommentCallBack;
import com.udangtangtang.emotion_mapfile.presenter.MyCommentCallBack;
import com.udangtangtang.emotion_mapfile.presenter.SetChartCallBack;
import com.udangtangtang.emotion_mapfile.presenter.StatisticsCallBack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private List<Comment> createCommentList(HashMap<String, Object> target) {
        long twoDay = 1000 * 60 * 60 * 24 * 2;
        Date nowDate = new Date();
        Long now_criteria = nowDate.getTime();
        Log.d(TAG, "현재시간" + String.valueOf(now_criteria));
        this.angryPeople = 0;
        this.happyPeople = 0;
        List<Comment> comments = new ArrayList<Comment>();

        for (String key : target.keySet()) {
            HashMap<String, String> userInfo = (HashMap) target.get(key);
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

        Comment[] comment_list = new Comment[comments.size()];
        for (int i = 0; i < comments.size(); i++) {
            comment_list[i] = comments.get(i);
        }

        sort_comment(comment_list, 0, comment_list.length - 1);

        comments.clear();

        comments.addAll(Arrays.asList(comment_list));

        this.temperature = angryPeople - happyPeople;
        return comments;
    }

    private void sort_comment(Comment[] comment_list, int left, int right) {
        int pl = left;
        int pr = right;
        Long mid = comment_list[(pl + pr) / 2].getCreate_at();

        do {
            //작성된 날짜값을 기준으로 비교함 - 최신순 우선
            while (comment_list[pl].getCreate_at() > mid) pl++;
            while (comment_list[pr].getCreate_at() < mid) pr--;
            if (pl <= pr) {
                swap(comment_list, pl++, pr--);
            }
        } while (pl <= pr);
        if (left < pr) sort_comment(comment_list, left, pr);
        if (left < right) sort_comment(comment_list, pl, right);
    }

    public void swap(Comment[] comment_list, int idx1, int idx2) {
        Comment comment = comment_list[idx1];
        comment_list[idx1] = comment_list[idx2];
        comment_list[idx2] = comment;
    }

    /**
     * 매개변수를 통해 City 객체의 속성들을 초기화하는 메소드
     *
     * @param myCity    자신이 현재 위치하고 있는 시
     * @param district  자신이 현재 위치하고 있는 구
     * @param latitude  자신이 현재 위치하고 있는 위도
     * @param longitude 자신이 현재 위치하고 있는 경도
     */
    public void setInitInfo(String myCity, String district, double latitude, double longitude) {
        this.myCity = myCity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.district = district;
    }

    /**
     * FirebaseDatabase에 자신의 Comment를 등록하는 메소드
     *
     * @param comment  등록하려는 Comment 객체
     * @param id       현재 로그인 되어있는 회원의 ID
     * @param callBack 작업이 완료되면 그 결과를 전달할 콜백함수
     * @throws Exception
     */
    public void insert_comment(Comment comment, String id, InsertCommentCallBack callBack) throws Exception {

        // comment의 위도, 경도, 구는 City 내의 정보들로 초기화.
        comment.setDistrict(district);
        comment.setLatitude(latitude);
        comment.setLongitude(longitude);

        try {
            DatabaseReference reference = firebaseDatabase.getReference("users").child(myCity);
            reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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

                        // 실제 db에 comment를 등록하는 라인
                        reference.child(id).setValue(comment);
                        // callBack을 통해 작업이 완료됐음을 알림 -> 매개변수로 이전 comment와 비교하여 상태가 바뀌었는지, 현재 등록한 comment의 상태를 전달
                        callBack.onSuccess(Optional.of(statusChanged), comment.getStatus());
                    });

                    // 비어있는 경우 새로운 comment를 db에 등록
                    if (!oldComment.isPresent()) {
                        reference.child(id).setValue(comment);

                        // callBack을 통해 작업이 완료 되었음을 알림. 이전 comment가 없기 때문에 첫 번째 매개변수는 false를 전달
                        callBack.onSuccess(Optional.empty(), comment.getStatus());
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

    /**
     * FirebaseDatabase로부터 자신의 위치를 기반으로 해당 도시의 Comment들을 읽어오는 메소드
     *
     * @param callBack 작업이 완료되면 그 결과를 전달할 콜백함수
     * @param id       현재 로그인 되어있는 회원의 ID
     */
    public void getCommentList(CommentListCallBack callBack, String id) {
        Log.d(TAG, "getCommentList: currentMill : " + System.currentTimeMillis() + " city.Mycity : " + myCity);
        // 레퍼런스 선언 및 이벤트 리스너 등록 -> SingleValueEvent로 등록한 이유는 별도의 새로고침을 통해서만 이 데이터가 보여주기 위함.
        DatabaseReference users = firebaseDatabase.getReference("users").child(myCity);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    // snapshot으로부터 데이터를 Optional 형태로 획득
                    Optional<HashMap<String, Object>> comments = Optional.ofNullable((HashMap) snapshot.getValue());
                    // comments 안에 데이터가 존재한다면 callBack을 통해 comments안의 데이터를 기반으로 Comment 객체로 이루어져 있는 List를 매개변수로 전달
                    comments.ifPresent(u -> callBack.onSuccess(Optional.of(createCommentList(u))));
                    if (!comments.isPresent()) {
                        // 데이터가 없다면 onFailed() 호출
                        Log.d(TAG, "사용자 위치에 comment없음");
                        callBack.onFailed();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "사용자 위치에 comment없음");
                    callBack.onFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    /**
     * FirebaseDatabase로부터 모든 도시의 Status(온도, 행복한 사람수, 화난 사람수)에 대한 정보를 읽어오는 메소드
     *
     * @param callback 작업이 완료되면 그 결과를 전달할 콜백함수
     */
    public void getStatistics(StatisticsCallBack callback, SetChartCallBack setChartCallBack) {
        // 레퍼런스 선언 및 이벤트 리스너 등록
        DatabaseReference stat = firebaseDatabase.getReference("status");
        stat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // snapshot으로 부터 HashMap 형태로 데이터를 읽어옴.
                Optional<HashMap> status = Optional.ofNullable((HashMap) snapshot.getValue());
                // callBack 함수를 통해 읽어온 status를 매개변수로 전달. -> 이후 데이터 처리는 MainPresenter에서 처리.
                DatabaseReference localStat = null;
                //status가 존재하면 -> DB 내에 어떤 데이터라도 존재함
                status.ifPresent(s -> {
                    setChartCallBack.SuccessGetStatus(status);
                    // 자신이 위치하고 있는 도시에 대한 DB가 존재하는 경우
                    if (s.get(myCity) != null)

                        callback.onSuccess(status, true);
                        // 자신이 위치하고 있는 도시에 대한 DB가 존재하지 않는 경우
                    else {
                        stat.child(myCity).child("happy_people").setValue(0);
                        stat.child(myCity).child("angry_people").setValue(0);
                        callback.onSuccess(status, false);
                    }
                });

                // DB 내에 진자 아무 데이터도 없음
                if (!status.isPresent()) {
                    // DB의 초기값을 설정해준 후, callback.onFailed() 메소드 호출
                    stat.child(myCity).child("happy_people").setValue(0);
                    stat.child(myCity).child("angry_people").setValue(0);
                    callback.onFailed();
                    setChartCallBack.OnFailGetStatus();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    /**
     * City의 insert_comment 메소드를 호출한 이후 해당 comment에 따라 해당 도시의 status(기온 등등)에 변화를 주는 메소드
     *
     * @param statusChanged 자신의 이전 Comment의 status가 바뀌었는지 즉, 감정이 변화되었는지 여부 -> Optional.empty()인 경우는 이전 comment가 없는 경우
     * @param status        자신이 현재 등록한 Comment의 status
     */
    public void changeStatus(Optional<Boolean> statusChanged, String status) {
        DatabaseReference statRef = firebaseDatabase.getReference("status").child(myCity);
        Log.d(TAG, "changeStatus: ");
        // 감정이 변화되었을 경우에만 작업을 처리함.
        statusChanged.ifPresent(isChanged -> {
            if (isChanged) {
                Log.d(TAG, "changeStatus: if entered");
                // 현재 위치하고 있는 도시의 status 레퍼런스 획득 및 이벤트 리스너 등록
                statRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        // HashMap 형태로 snapshot으로 부터 데이터를 읽어들임.
                        HashMap<String, Long> stats = (HashMap) snapshot.getValue();
                        // 등록한 comment의 status가 빡침일 경우 -> 해당 도시의 화난 사람 += 1, 기쁜 사람 -= 1
                        if (status.equals("빡침")) {
                            statRef.child("angry_people").setValue(stats.get("angry_people") + 1);
                            statRef.child("happy_people").setValue(stats.get("happy_people") - 1);
                        } else {
                            // 등록한 comment의 status가 기쁨일 경우 -> 해당 도시의 화난 사람 -= 1, 기쁜 사람 += 1
                            statRef.child("angry_people").setValue(stats.get("angry_people") - 1);
                            statRef.child("happy_people").setValue(stats.get("happy_people") + 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: ");
                    }
                });
            }
        });
        // comment를 처음 등록하셨군요!!
        if (!statusChanged.isPresent()) {
            statRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    HashMap<String, Long> stats = (HashMap) snapshot.getValue();
                    // 등록한 comment의 status가 빡침일 경우 -> 해당 도시의 화난 사람 += 1, 기온 += 1
                    if (status.equals("빡침")) {
                        statRef.child("angry_people").setValue(stats.get("angry_people") + 1);
                    } else {
                        // 등록한 comment의 status가 기쁨일 경우 -> 해당 도시의 기쁜 사람 += 1, 기온 -= 1
                        statRef.child("happy_people").setValue(stats.get("happy_people") + 1);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: ");
                }
            });
        }
    }

    /**
     * Comment를 등록한 경우 MainActivity에 존재하는 최근 정보를 갱신하기 위한 메소드
     *
     * @param id       현재 로그인 되어있는 유저의 ID
     * @param callBack 작업이 완료되면 그 결과를 전달할 콜백함수
     */
    public void addMyCommentListener(String id, MyCommentCallBack callBack) {
        // 자신의 Comment에 대한 레퍼런스 획득 및 이벤트 리스너 등록
        //이메일 정보제공 미 동의시
        if (id == null) {
            callBack.onSuccess("kakao login 이메일 정보 제공 미동의", "이메일 정보제공 미동의시 감정표현이 불가능 합니다");
            Log.d(TAG, "kakao 이메일 정보 제공 미동의");
        } else {
            DatabaseReference myInfo = firebaseDatabase.getReference("users").child(myCity).child(id);
            myInfo.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    // HashMap 형태로 snapshot으로부터 데이터를 읽어들임.
                    Optional<HashMap> myComment = Optional.ofNullable((HashMap) snapshot.getValue());
                    // callBack 함수를 통해 자신의 최신 status와 comment를 매개변수로 전달.
                    myComment.ifPresent(c -> callBack.onSuccess(String.valueOf(c.get("status")), String.valueOf(c.get("comment"))));
                    if (!myComment.isPresent()) {
                        callBack.onFailed();
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: ");
                }
            });
        }
    }
}