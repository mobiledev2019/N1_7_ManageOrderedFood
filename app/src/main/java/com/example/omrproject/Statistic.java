package com.example.omrproject;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omrproject.Common.Common;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;

import com.github.mikephil.charting.components.AxisBase;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistic extends AppCompatActivity{

    CombinedChart mChart;
    BarChart bChart;
    TextView txtLoginTime, txtAccumTime ;
    private Handler handler = new Handler();
    private Runnable runnable;

    private String DATE_FORMAT = "HH:mm:ss";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        //get charts
        bChart = (BarChart) findViewById(R.id.barchart);
        mChart = (CombinedChart) findViewById(R.id.combinedChart);
        txtLoginTime = (TextView) findViewById(R.id.txtLoginTime);
        txtAccumTime = (TextView) findViewById(R.id.txtAccumTime);

        //init login time
        String dateFormat = "dd/MM/yyyy HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        txtLoginTime.setText(formatter.format(new Date(Common.loginTime)));
        //init statistic time
        countWorkTime();
        //init tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_statistic);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //init Chart
        setmChart();
        setbChart();
    }

    public void setbChart(){

        bChart.getDescription().setText("");
        YAxis leftAxis = bChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = bChart.getAxisRight();
        rightAxis.setEnabled(false);

        bChart.setBackgroundColor(Color.parseColor("#131313"));
        bChart.setDrawGridBackground(false);
        final List<String> xLabel = new ArrayList<>();
        xLabel.add("Mon");
        xLabel.add("Tues");
        xLabel.add("Wed");
        xLabel.add("Thurs");
        xLabel.add("Fri");
        xLabel.add("Sat");
        xLabel.add("Sun");

        int[] data = new int[] { 2, 4, 6, 8, 8, 6, 5};
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        for (int index = 0; index < data.length; index++) {
            entries.add(new BarEntry(index, data[index]));
        }

        //set Label
        XAxis xAxis = bChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int) value % xLabel.size());
            }
        });

        //set data
        BarDataSet barDataset = new BarDataSet(entries, "");
        barDataset.setColors(Color.parseColor("#8A7669"));
        BarData barData = new BarData(barDataset);
        barData.setBarWidth(0.5f);
        barData.setValueTextColor(Color.parseColor("#ffffff"));

        bChart.setData(barData);
//        xAxis.setAxisMaximum(barData.getXMax() + 1f);
        bChart.animateX(1500, Easing.EaseInCubic);
        bChart.animateY(1500, Easing.EaseInCubic);
    }
    public void setmChart(){
        mChart.getDescription().setText("Doanh thu trong tuần của quán");

        final List<String> xLabel = new ArrayList<>();
        xLabel.add("Mon");
        xLabel.add("Tues");
        xLabel.add("Wed");
        xLabel.add("Thurs");
        xLabel.add("Fri");
        xLabel.add("Sat");
        xLabel.add("Sun");

        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.parseColor("#131313"));
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
              public void onValueSelected(Entry e, Highlight h) {
                      Toast.makeText(Statistic.this, "Tổng doanh thu: "
                              + e.getY()
                              + ", Thứ: "
                              + xLabel.get((int)h.getX())
                              , Toast.LENGTH_SHORT).show();
              }
              @Override
              public void onNothingSelected() {

              }
        });
        mChart.setHighlightFullBarEnabled(false);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.WHITE);




        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int) value % xLabel.size());
            }
        });

        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) dataChart());

        data.setData(lineDatas);

//        xAxis.setAxisMaximum(data.getXMax() + 1f);

        mChart.setData(data);
        mChart.invalidate();
        mChart.animateX(1500, Easing.EaseInCubic);
        mChart.animateY(1500, Easing.EaseInCubic);
    }
    private static DataSet dataChart() {

        LineData d = new LineData();
        int[] data = new int[] { 200000, 365000, 879000, 1350000, 2400000, 1430000, 1576000};

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < data.length; index++) {
            entries.add(new Entry(index, data[index]));
        }

        LineDataSet set = new LineDataSet(entries, "");

        set.setColor(Color.GREEN);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.GREEN);
        set.setCircleRadius(5f);
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }

    private void countWorkTime() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String time ="";
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Long current_time = System.currentTimeMillis();
                    long diff = current_time - Common.loginTime;
                    long Days = diff / (24 * 60 * 60 * 1000);
                    long Hours = diff / (60 * 60 * 1000) % 24;
                    long Minutes = diff / (60 * 1000) % 60;
                    long Seconds = diff / 1000 % 60;
                    time = String.format("%02d",Hours) + ":" + String.format("%02d",Minutes) + ":" + String.format("%02d",Seconds);
                    txtAccumTime.setText(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}
