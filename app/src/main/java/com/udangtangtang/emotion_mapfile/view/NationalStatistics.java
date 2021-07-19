package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.NationalStatisticsPresenter;

import java.util.ArrayList;

public class NationalStatistics extends Activity {
    private LinearLayout linearLayout;
    private NationalStatisticsPresenter nationalStatisticsPresenter;
    private ArrayList<Integer> valueList = new ArrayList<>(); // ArrayList 선언
    private ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nationalstatistics);

        init((Boolean) getIntent().getSerializableExtra("isSunny"));
        nationalStatisticsPresenter.setActivity(this);
        //표 세팅
        nationalStatisticsPresenter.add_tableRow();

        Utils.init(NationalStatistics.this);
        graphInitSetting();       //그래프 기본 세팅

        barChart.setTouchEnabled(false);
        barChart.getAxisRight().setAxisMaxValue(100);
        barChart.getAxisLeft().setAxisMaxValue(100);
        barChart.setDrawValueAboveBar(false);
    }

    public void init(boolean isSunny) {
        Intent intent = getIntent();
        linearLayout = findViewById(R.id.statistics_layout);
        nationalStatisticsPresenter = (NationalStatisticsPresenter) intent.getSerializableExtra("nationalStatisticsPresenter");
        linearLayout = findViewById(R.id.linearLayout_menu3);
        barChart = (BarChart) findViewById(R.id.charting);

        if (isSunny) {
            linearLayout.setBackground(getResources().getDrawable(R.drawable.clear_sky, null));
        } else {
            linearLayout.setBackground(getResources().getDrawable(R.drawable.cloudy, null));
        }

    }


    public void graphInitSetting() {

        //그래프 X축 받아오기
        labelList = nationalStatisticsPresenter.get_label();
        //그래프 Y축 받아오기
        valueList = nationalStatisticsPresenter.get_value();
        BarChartGraph(labelList, valueList);
        barChart.setTouchEnabled(false);
        barChart.getAxisRight().setAxisMaxValue(100);
        barChart.getAxisLeft().setAxisMaxValue(100);
    }

    /**
     * 그래프함수
     */
    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Integer> valList) {

        // BarChart 메소드
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry((Integer) valList.get(i), i));
        }

        BarDataSet depenses = new BarDataSet(entries, "빡친 도시 Top5"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        barChart.setDescription(" ");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }

        BarData data = new BarData(labels, depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.LIBERTY_COLORS); //

        barChart.setData(data);
        barChart.animateXY(100, 100);
        barChart.invalidate();
    }

    public void add_chartRow(String name, int angry_count, int happy_count, int total, int index) {
        //LinearLayout 생성
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        linearLayout.setLayoutParams(layoutParams);

        //한 행에 들어갈 textView 4개 생성
        TextView[] textViews = {new TextView(this), new TextView(this), new TextView(this), new TextView(this)};
        for (int i = 0; i < textViews.length; i++) {
            textView_setting(textViews[i], index);
        }

        textViews[0].setText(name);
        textViews[1].setText(String.valueOf(angry_count + "명"));
        textViews[2].setText(String.valueOf(happy_count + "명"));
        textViews[3].setText(String.valueOf(total + "명"));

        for (TextView textView : textViews) {
            linearLayout.addView(textView);
        }
        this.linearLayout.addView(linearLayout);
    }

    private void textView_setting(TextView textview, int index) {
        //TextView 속성 설정을 위한 layoutParams 생성
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 105, 1);
        layoutParams.leftMargin = 14;
        layoutParams.bottomMargin = 5;
        if (index % 2 == 0)
            textview.setBackground(ContextCompat.getDrawable(this, R.drawable.round_border));
        textview.setGravity(17);
        textview.setLayoutParams(layoutParams);
        textview.setPadding(3, 3, 3, 3);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
