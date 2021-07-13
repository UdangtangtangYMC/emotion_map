package com.udangtangtang.emotion_mapfile.presenter;

import android.util.Log;
import android.widget.Toast;

import com.udangtangtang.emotion_mapfile.model.CityStatus;
import com.udangtangtang.emotion_mapfile.view.NationalStatistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NationalStatisticsPresenter implements Serializable {
    private final String TAG = "NationalStatisticsPresenter";
    private List<CityStatus> cityStatuses;
    private NationalStatistics activity;

    public NationalStatisticsPresenter(List<CityStatus> cityStatuses){
        this.cityStatuses = cityStatuses;
    }

    public void setActivity(NationalStatistics activity){
        this.activity = activity;
    }

    public void add_tableRow(){
        int index = 0;
        Log.d(TAG, "cityStatuses size = " + String.valueOf(cityStatuses.size()));
        try {
            for (CityStatus cityStatus : cityStatuses) {
                activity.add_chartRow(cityStatus.getName(), cityStatus.getAngry_count(),
                        cityStatus.getHappy_count(), cityStatus.getTotal(), index);
                index++;
            }
        }catch (NullPointerException e){
            Log.e(TAG, e.toString());
            Toast.makeText(activity, "표를 불러올 수 없음", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> get_label(){
        ArrayList<String> label_list = new ArrayList<String>();
        if(cityStatuses.size() < 5){
            for(int i=0;i<cityStatuses.size();i++){
                label_list.add(cityStatuses.get(i).getName());
            }
        }else{
            for(int i=0;i<5;i++){
                label_list.add(cityStatuses.get(i).getName());
            }
        }
        return label_list;
    }

    public ArrayList<Integer> get_value(){
        ArrayList<Integer> value_list = new ArrayList<Integer>();
        int angry_value = 0;
        int happy_value = 0;
        int total = 0;
        if(cityStatuses.size() < 5){
            for(int i=0;i<cityStatuses.size();i++){
                angry_value = cityStatuses.get(i).getAngry_count();
                happy_value = cityStatuses.get(i).getHappy_count();
                total = angry_value + happy_value;
                try{
                    value_list.add(angry_value/total * 100);
                }catch (ArithmeticException e){
                    Log.d(TAG, "divide by zero");
                }
            }
        }else{
            for(int i=0;i<5;i++){
                angry_value = cityStatuses.get(i).getAngry_count();
                happy_value = cityStatuses.get(i).getHappy_count();
                total = angry_value + happy_value;
                value_list.add(angry_value/total * 100);
            }
        }
        return value_list;
    }

}
