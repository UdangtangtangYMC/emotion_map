package com.udangtangtang.emotion_mapfile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.NationalStatisticsPresenter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NationalStatistics extends Activity{
    private NationalStatisticsPresenter nationalStatisticsPresenter;
    private ArrayList<Integer> valueList = new ArrayList<>(); // ArrayList 선언
    private ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
    private BarChart barChart;
    private TextView minuteTextview;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nationalstatistics);

        init();
        nationalStatisticsPresenter.setActivity(this);
        //표 세팅
        nationalStatisticsPresenter.add_tableRow();

        Utils.init(NationalStatistics.this);
        System.out.println(barChart);
        barChart = (BarChart) findViewById(R.id.charting);
        System.out.println(barChart);
        graphInitSetting();       //그래프 기본 세팅

        barChart.setTouchEnabled(false);
        barChart.getAxisRight().setAxisMaxValue(80);
        barChart.getAxisLeft().setAxisMaxValue(80);
    }

    public void init(){
        Intent intent = getIntent();
        nationalStatisticsPresenter = (NationalStatisticsPresenter) intent.getSerializableExtra("nationalStatisticsPresenter");
        tableLayout = findViewById(R.id.tablelayout);

    }


    public void graphInitSetting(){

        //그래프 X축 받아오기
        labelList = nationalStatisticsPresenter.get_label();
        //그래프 Y축 받아오기
        valueList = nationalStatisticsPresenter.get_value();
        BarChartGraph(labelList, valueList);
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

        BarDataSet depenses = new BarDataSet(entries, "빡친 도시 Top5"); // 변수로 받아서 넣어줘도 됨
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

    public void add_tableRow(String name, int angry_count, int happy_count, int total, int index){
        //tableRow 생성
        TableRow tableRow = new TableRow(this);
        //tableRow의 속성 설정을 위한 LayoutParams 생성
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        layoutParams.bottomMargin = 5;
        tableRow.setLayoutParams(layoutParams);

        //한 행에 들어갈 textView 4개 생성
        TextView[] textViews = {new TextView(this), new TextView(this), new TextView(this), new TextView(this)};
        for(int i=0;i<textViews.length;i++){
            textView_setting(textViews[i], index);
        }

        textViews[0].setText(name);
        textViews[1].setText(String.valueOf(angry_count + "명"));
        textViews[2].setText(String.valueOf(happy_count+"명"));
        textViews[3].setText(String.valueOf(total + "명"));
    }

    private void textView_setting(TextView textview, int index){
        //TextView 속성 설정을 위한 layoutParams 생성
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, R.dimen.chart_txtHeight);
        layoutParams.leftMargin = R.dimen.chart_marginLeft;
        layoutParams.bottomMargin = 1;
        if(index % 2 == 0)
            textview.setBackground(ContextCompat.getDrawable(this,R.drawable.round_border1));
        layoutParams.gravity = 17; // 17 - center\
        textview.setLayoutParams(layoutParams);
        textview.setPadding(3, 3, 3, 3);
    }
}
