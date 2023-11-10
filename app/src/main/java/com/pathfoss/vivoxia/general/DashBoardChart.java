package com.pathfoss.vivoxia.general;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class DashBoardChart {

    private final BarChart barChart;
    private final List<BarEntry> barEntryList;
    private final float goalValue;
    private final String dataType;

    // Create constructor method
    public DashBoardChart(BarChart barChart, List<BarEntry> barEntryList, float goalValue, String dataType) {
        this.barChart = barChart;
        this.barEntryList = barEntryList;
        this.goalValue = goalValue;
        this.dataType = dataType;
    }

    public void updateBarChart() {

        // Set bar data
        BarData barData = new BarData();

        int size = barEntryList.size();
        for (int i = 1; i <= 7 - size; i++) {
            barEntryList.add(new BarEntry(-i, 0f));
        }

        BarDataSet barDataSet = new BarDataSet(barEntryList,null);
        barDataSet.setColor(Controller.getColorVivoxia());

        barData.addDataSet(barDataSet);
        barData.setBarWidth(0.3f);
        barData.setValueTextColor(Color.WHITE);
        barData.setValueTextSize(11);
        barData.setValueFormatter(new ChartLabelFormatter(dataType));

        // Create LimitLine
        LimitLine limitLine = new LimitLine(goalValue);
        limitLine.setLineWidth(1f);
        limitLine.setLineColor(Controller.getColorNaturalWhite());

        // Modify left axis
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.addLimitLine(limitLine);
        yAxisLeft.setAxisMinimum((float) Math.min(barDataSet.getYMin() * 0.99, goalValue * 0.99));
        yAxisLeft.setAxisMaximum((float) Math.max(barData.getYMax() + (barDataSet.getYMax() - yAxisLeft.getAxisMinimum()) * 0.15, goalValue));
        yAxisLeft.setGridLineWidth(1f);
        yAxisLeft.setGridColor(Controller.getColorVivoxiaForeground());
        yAxisLeft.setDrawLimitLinesBehindData(true);

        // Set BarChart graphics
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.setViewPortOffsets(0, 0, 0, 0);

        // Draw BarChart
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.setData(barData);
        barChart.setRenderer(new RoundBarChartRenderer(barChart, barChart.getAnimator(), barChart.getViewPortHandler()));
        barChart.invalidate();
    }
}