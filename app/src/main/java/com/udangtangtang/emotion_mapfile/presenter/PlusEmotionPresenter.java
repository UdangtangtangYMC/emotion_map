package com.udangtangtang.emotion_mapfile.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.udangtangtang.emotion_mapfile.model.City;
import com.udangtangtang.emotion_mapfile.model.Comment;
import com.udangtangtang.emotion_mapfile.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Optional;

public class PlusEmotionPresenter extends LocationCallback {
    private final String TAG = "PlusEmotionPresenter";
    private Context context;
    private final City city;
    private final User user;
    private FusedLocationProviderClient loc;

    public PlusEmotionPresenter(Context context, City city, User user) {
        this.context = context;
        this.city = city;
        this.user = user;
        this.loc = LocationServices.getFusedLocationProviderClient(context);
    }

    public String get_emotion(int id, int happy_id) {
        return (id == happy_id) ? "기쁨" : "빡침";
    }

    public void insert_emotion(String selected_emotion, String comment) {
        Comment input_comment = new Comment();

        input_comment.setComment(comment);
        input_comment.setStatus(selected_emotion);
        input_comment.setCreate_at(get_date());


        try {
            updateLocation(loc);
            // 새로 등록하려는 comment와 user.getID()를 매개변수로 city.insertComment 메소드 호출
            city.insert_comment(input_comment, user.getID(), createInsertCommentCallBack());
        } catch (Exception e) {
            Toast.makeText(context, "감정 입력 오류", Toast.LENGTH_SHORT).show();
        }
    }

    public Long get_date() {
        Date time = new Date();
        return time.getTime();
    }

    private InsertCommentCallBack createInsertCommentCallBack() {
        return new InsertCommentCallBack() {
            @Override
            public void onSuccess(Optional<Boolean> statusChanged, String status) {
                city.changeStatus(statusChanged, status);
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void updateLocation(FusedLocationProviderClient loc) {
        Log.d(TAG, "updateLocation: plusEmotion");
        Task<Location> currentLocation = loc.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                Log.d(TAG, "isCancellationRequested: ");
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull @NotNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Location> task) {
                Location result = task.getResult();
                Log.d(TAG, "onComplete: " + result.getLatitude() + " " + result.getLongitude());
            }
        });
    }

    @Override
    public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
        Location lastLocation = locationResult.getLastLocation();
        Log.d(TAG, "onLocationResult: " + lastLocation.getLatitude() + " " + lastLocation.getLongitude());
    }
}
