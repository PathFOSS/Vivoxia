package com.pathfoss.vivoxia.body;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.DashBoardChart;
import com.pathfoss.vivoxia.general.Units;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class BodyDashboard extends Fragment {

    private final List<BarEntry> barEntryList = new ArrayList<>();
    private LinkedHashMap<String, Float> linkedHashMap;

    private String textValue;
    private String goalText;
    private String dataType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMeasures();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_body_dashboard, container, false);

        // Define and assign views
        ConstraintLayout clTargets = view.findViewById(R.id.cl_targets);
        BarChart bc = view.findViewById(R.id.bc);
        ((TextView) clTargets.findViewById(R.id.tv_title)).setText(getString(R.string.at_latest));
        ((TextView) clTargets.findViewById(R.id.tv_value)).setText(textValue);
        ((TextView) clTargets.findViewById(R.id.tv_goal)).setText(goalText);

        // Create the chart view
        new DashBoardChart(bc, barEntryList, Units.getBodyChartItemGoal(), dataType).updateBarChart();

        return view;
    }

    // Create method to fetch and modify database values
    private void setMeasures() {

        // Set measurement unit and get body measure data from the database
        dataType = Units.getBodyDataType();
        linkedHashMap = Controller.getBodyJournalDataBase().getBodyJournalEntriesByQuery(Units.getBodyChartItem());
        Iterator<String> iterator = linkedHashMap.keySet().iterator();

        // Convert data into BarChart readable format
        for (float i = 0f; i < linkedHashMap.size(); i++) {
            barEntryList.add(new BarEntry(i, getFromHashMap(iterator.next())));
        }

        // Define default latest values
        float latestValue = 0f;
        float goalValue = Units.fromMetric(Units.getBodyChartItemGoal(), dataType);


        if (barEntryList.size() > 0) {
            latestValue = Units.fromMetric(barEntryList.get(barEntryList.size()-1).getY(), dataType);
        }

        // Set displayed text
        String textLatestValue = Units.decimalFormat.format(latestValue);
        String textGoalValue = Units.decimalFormat.format(goalValue);

        if (dataType.equals("ft")) {
            textLatestValue = Units.toHeightIfApplicable((int) latestValue, dataType);
            textGoalValue = Units.toHeightIfApplicable((int) goalValue, dataType);
        }

        textValue = textLatestValue;
        goalText = textGoalValue + " " + dataType;
    }

    // Create null-safe method to get values from HashMaps
    @NonNull
    private Float getFromHashMap(String key) {
        Float output = linkedHashMap.get(key);
        return output != null ? output : 0f;
    }
}