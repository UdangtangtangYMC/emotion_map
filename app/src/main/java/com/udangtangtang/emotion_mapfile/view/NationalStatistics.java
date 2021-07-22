package com.udangtangtang.emotion_mapfile.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.NationalStatisticsPresenter;

import java.util.ArrayList;
import java.util.List;

public class NationalStatistics extends AppCompatActivity {
    private static final String TAG = "NationalStatics";
    private LinearLayout linearLayout;
    private LinearLayout linearLayout_chart;
    private NationalStatisticsPresenter nationalStatisticsPresenter;
    private ArrayList<Integer> valueList = new ArrayList<>(); // ArrayList 선언
    private ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
    private BarChart barChart;
    private int clearSky;
    private int cloudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nationalstatistics);

        Toolbar toolbar = findViewById(R.id.toolbar_statis);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        init((boolean)getIntent().getSerializableExtra("isSunny"));
        nationalStatisticsPresenter.setActivity(this);
        //표 세팅
        nationalStatisticsPresenter.add_tableRow();

        Utils.init(NationalStatistics.this);
        graphInitSetting();       //그래프 기본 세팅

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init(boolean isSunny) {
        Intent intent = getIntent();
        linearLayout = findViewById(R.id.statistics_layout);
        linearLayout_chart = findViewById(R.id.linearLayout_menu3);
        nationalStatisticsPresenter = (NationalStatisticsPresenter) intent.getSerializableExtra("nationalStatisticsPresenter");
        barChart = (BarChart) findViewById(R.id.charting);
        clearSky = getResources().getColor(R.color.clearSky_upper_gradient);
        cloudy = getResources().getColor(R.color.cloudy_upper_gradient);
        if (isSunny) {
            linearLayout.setBackground(getResources().getDrawable(R.drawable.clear_sky, null));
            Log.d(TAG, "isSunny");
            setStatusBarColor(clearSky);
        } else{
            linearLayout.setBackground(getResources().getDrawable(R.drawable.cloudy, null));
            Log.d(TAG, "isCloudy");
            setStatusBarColor(cloudy);
        }

    }

    private void setStatusBarColor(int color){
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
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
        depenses.setValueTextSize(20);
//        int[] color_list = {Color.RED, Color.RED,Color.RED, Color.RED, Color.RED};
        depenses.setColors(new int[] {Color.rgb(000, 255, 255), Color.rgb(051, 255, 255), Color.rgb(102, 255, 255),
                        Color.rgb(153, 255, 255), Color.rgb(204, 204, 204)});
        barChart.setDescription(" ");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }

        BarData data = new BarData(labels, depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        data.setValueTextSize(20);

        barChart.setData(data);
        barChart.animateXY(100, 100);
        barChart.getBarData().setValueTextSize(15);
        barChart.setTouchEnabled(false);
        barChart.getAxisLeft().setAxisMaxValue(100);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.setGridBackgroundColor(Color.WHITE);
        barChart.setDrawValueAboveBar(false);

        Legend l = barChart.getLegend();
        l.setTextSize(15);
        l.setForm(Legend.LegendForm.CIRCLE);
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
        this.linearLayout_chart.addView(linearLayout);
    }

    private void textView_setting(TextView textview, int index) {
        //TextView 속성 설정을 위한 layoutParams 생성
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 105, 1);
        layoutParams.leftMargin = 14;
        layoutParams.bottomMargin = 5;
        textview.setLayoutParams(layoutParams);
        textview.setGravity(17);
        textview.setTextColor(getResources().getColor(R.color.white,null));
        if (index % 2 == 0){
            textview.setTextColor(getResources().getColor(R.color.black,null));
            textview.setBackground(ContextCompat.getDrawable(this, R.drawable.round_border));
        }
        textview.setPadding(3, 3, 3, 3);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
