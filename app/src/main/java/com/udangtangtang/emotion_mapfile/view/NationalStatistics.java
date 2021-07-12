package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.udangtangtang.emotion_mapfile.R;

import java.util.ArrayList;

public class NationalStatistics extends Activity{
    ArrayList<Integer> jsonList = new ArrayList<>(); // ArrayList 선언
    ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
    BarChart barChart;
    TextView minuteTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nationalstatistics);

        Utils.init(NationalStatistics.this);
        System.out.println(barChart);
        barChart = (BarChart) findViewById(R.id.charting);
        System.out.println(barChart);
        graphInitSetting();       //그래프 기본 세팅

        barChart.setTouchEnabled(false);
        barChart.getAxisRight().setAxisMaxValue(80);
        barChart.getAxisLeft().setAxisMaxValue(80);
    }
    public void graphInitSetting(){

        labelList.add("일");
        labelList.add("월");
        labelList.add("화");
        labelList.add("수");
        labelList.add("목");
        labelList.add("금");
        labelList.add("토");

        jsonList.add(10);
        jsonList.add(20);
        jsonList.add(30);
        jsonList.add(40);
        jsonList.add(50);
        jsonList.add(60);
        jsonList.add(70);


        BarChartGraph(labelList, jsonList);
        barChart.setTouchEnabled(false);
        barChart.getAxisRight().setAxisMaxValue(80);
        barChart.getAxisLeft().setAxisMaxValue(80);
    }

    /**
     * 그래프함수
     */
    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Integer> valList) {

        // BarChart 메소드
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry(valList.get(i), i));
        }

        BarDataSet depenses = new BarDataSet(entries, "일일 사용시간"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        Description description = new Description();
        description.setText("description");
        barChart.setDescription(description);

        BarData data = new BarData(depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.LIBERTY_COLORS); //

        barChart.setData(data);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();
    }
}
