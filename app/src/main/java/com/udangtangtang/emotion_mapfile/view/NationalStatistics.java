package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.udangtangtang.emotion_mapfile.R;

import java.util.ArrayList;
import java.util.List;

public class NationalStatistics extends Activity {
    private BarChart barChart;
    private List<String> labelList = new ArrayList<>(); // 항목
    private List<Integer> value_list = new ArrayList<Integer>(); // 값

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nationalstatistics);

        initView();
        graphInitSetting(); //그래프 기본 세팅


    }

    private void initView(){
        barChart = findViewById(R.id.chart);
    }

   public void graphInitSetting(){

   }
}
