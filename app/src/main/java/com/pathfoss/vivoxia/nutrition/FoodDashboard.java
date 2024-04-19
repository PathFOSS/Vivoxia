package com.pathfoss.vivoxia.nutrition;

import android.content.SharedPreferences;
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

public class FoodDashboard extends Fragment {

    private final List<BarEntry> barEntryList = new ArrayList<>();
    private final FoodJournalDataBase foodJournalDataBase = Controller.getFoodJournalDataBase();
    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();

    private LinkedHashMap<String, Float> linkedHashMap;

    private String dataType;
    private String today;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        today = Controller.getDataBaseDateFormat().format(System.currentTimeMillis());
        setMeasures();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflate, define, and modify views
        View view = inflater.inflate(R.layout.fragment_food_dashboard, container, false);

        BarChart bc = view.findViewById(R.id.bc);

        ConstraintLayout clProtein = view.findViewById(R.id.cl_protein);
        ConstraintLayout clFat = view.findViewById(R.id.cl_fat);
        ConstraintLayout clCarbs = view.findViewById(R.id.cl_carbs);
        ConstraintLayout clCalories = view.findViewById(R.id.cl_calories);

        ((TextView) clProtein.findViewById(R.id.tv_title)).setText(getString(R.string.protein_capitalized));
        ((TextView) clFat.findViewById(R.id.tv_title)).setText(getString(R.string.fat_capitalized));
        ((TextView) clCarbs.findViewById(R.id.tv_title)).setText(getString(R.string.carbs_capitalized));
        ((TextView) clCalories.findViewById(R.id.tv_title)).setText(getString(R.string.calories_capitalized));

        TextView tvProteinValue = clProtein.findViewById(R.id.tv_value);
        TextView tvFatValue = clFat.findViewById(R.id.tv_value);
        TextView tvCarbsValue = clCarbs.findViewById(R.id.tv_value);
        TextView tvCaloriesValue = clCalories.findViewById(R.id.tv_value);

        TextView tvProteinGoal = clProtein.findViewById(R.id.tv_goal);
        TextView tvFatGoal = clFat.findViewById(R.id.tv_goal);
        TextView tvCarbsGoal = clCarbs.findViewById(R.id.tv_goal);
        TextView tvCaloriesGoal = clCalories.findViewById(R.id.tv_goal);

        tvProteinValue.setText(getValueText("PROTEIN", " g"));
        tvFatValue.setText(getValueText("FAT", " g"));
        tvCarbsValue.setText(getValueText("CARBS", " g"));
        tvCaloriesValue.setText(getValueText("CALORIES", " kcal"));

        tvProteinGoal.setText(getGoalText(getString(R.string.protein_capitalized), " g"));
        tvFatGoal.setText(getGoalText(getString(R.string.fat_capitalized), " g"));
        tvCarbsGoal.setText(getGoalText(getString(R.string.carbs_capitalized), " g"));
        tvCaloriesGoal.setText(getGoalText(getString(R.string.calories_capitalized), " kcal"));

        // Create the chart view
        new DashBoardChart(bc, barEntryList, Units.getFoodChartItemGoal(), dataType).updateBarChart();

        return view;
    }

    // Create method to assign text to each value TextView
    @NonNull
    private String getValueText (String COL_QUERIED, String suffix) {
        return Units.decimalFormat.format(foodJournalDataBase.getDailySum(COL_QUERIED, today));
    }

    // Create method to assign text to each goal TextView
    @NonNull
    private String getGoalText (String name, String suffix) {
        return Units.decimalFormat.format(sharedPreferences.getFloat(name, 0f)) + suffix;
    }

    // Create method to fetch and modify database values
    private void setMeasures() {

        // Set measurement unit and get food measure data from the database
        dataType = Units.getFoodDataType();
        linkedHashMap = foodJournalDataBase.getFoodJournalEntriesByQuery(Units.getFoodChartItem());

        // Convert data into BarChart readable format
        if (linkedHashMap != null) {
            Iterator<String> iterator = linkedHashMap.keySet().iterator();

            for (float i = 0f; i < linkedHashMap.size(); i++) {
                barEntryList.add(new BarEntry(i, getFromHashMap(iterator.next())));
            }
        }
    }

    // Create null-safe method to get values from HashMaps
    @NonNull
    private Float getFromHashMap(String key) {
        Float output = linkedHashMap.get(key);
        return output != null ? output : 0f;
    }
}