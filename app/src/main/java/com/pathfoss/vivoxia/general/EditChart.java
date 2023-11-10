package com.pathfoss.vivoxia.general;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.body.BodyDashboard;
import com.pathfoss.vivoxia.exercise.ExerciseDashboard;
import com.pathfoss.vivoxia.nutrition.FoodDashboard;

import java.util.Objects;

public class EditChart extends DialogFragment {

    private View view;
    private AutoCompleteTextView autoCompleteTextView;

    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();
    private final SharedPreferences.Editor sharedPreferencesEditor = Controller.getSharedPreferencesEditor();

    private final String[] foodMeasureList = {
            "Calories",
            "Protein",
            "Fat",
            "Carbs"
    };

    private final String[] bodyMeasureList = {
            "Left Calf",
            "Right Calf",
            "Left Thigh",
            "Right Thigh",
            "Left Forearm",
            "Right Forearm",
            "Left Bicep",
            "Right Bicep",
            "Hips",
            "Waist",
            "Chest",
            "Shoulders",
            "Neck",
            "Weight",
            "Clavicles",
            "Body Fat"
    };

    private final String[] exerciseMeasureList = {
            "Workouts / Week",
            "Volume / Workout",
            "Reps / Set",
            "Sets / Workout",
            "Workout Time",
            "Rest / Set",
            "Work / Set",
            "Effort / Set"
    };

    private final DialogListener dialogListener;

    private final String key;
    private final String defaultValue;

    // Create constructor method
    public EditChart(DialogListener dialogListener, String key, String defaultValue) {
        this.dialogListener = dialogListener;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialog_edit_chart, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set window parameters and create a dropdown based on current Fragment
        ReusableMethods.setDialogWindowParameters(Objects.requireNonNull(getDialog()));
        switch (key) {
            case "FoodChartItem": {
                autoCompleteTextView = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), view.findViewById(R.id.act), foodMeasureList, 0);
                autoCompleteTextView.setText(sharedPreferences.getString(key, defaultValue), false);
                autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                    sharedPreferencesEditor.putString(key, foodMeasureList[position]).apply();
                    getParentFragmentManager().beginTransaction().replace(R.id.fcv_main, new FoodDashboard()).commit();
                    dialogListener.onCloseDialog(sharedPreferences.getString(key, defaultValue) + " (" + Units.getFoodDataType() + ")");
                    dismiss();
                });
                break;
            }
            case "ExerciseChartItem": {
                autoCompleteTextView = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), view.findViewById(R.id.act), exerciseMeasureList, 0);
                autoCompleteTextView.setText(sharedPreferences.getString(key, defaultValue), false);
                autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                    sharedPreferencesEditor.putString(key, exerciseMeasureList[position]).apply();
                    getParentFragmentManager().beginTransaction().replace(R.id.fcv_main, new ExerciseDashboard()).commit();
                    String titleText = sharedPreferences.getString(key, defaultValue) + " (" + Units.getExerciseDataType() + ")";
                    if (Units.getExerciseDataType().equals("")) {
                        titleText = sharedPreferences.getString(key, defaultValue);
                    }
                    dialogListener.onCloseDialog(titleText);
                    dismiss();
                });
                break;
            }
            case "BodyChartItem": {
                autoCompleteTextView = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), view.findViewById(R.id.act), bodyMeasureList, 0);
                autoCompleteTextView.setText(sharedPreferences.getString(key, defaultValue), false);
                autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                    sharedPreferencesEditor.putString(key, bodyMeasureList[position]).apply();
                    getParentFragmentManager().beginTransaction().replace(R.id.fcv_main, new BodyDashboard()).commit();
                    dialogListener.onCloseDialog(sharedPreferences.getString(key, defaultValue) + " (" + Units.getBodyDataType() + ")");
                    dismiss();
                });
                break;
            }
        }
    }

    // Create method to prepare for stopping dialog
    @Override
    public void onStop() {
        super.onStop();
        autoCompleteTextView.clearListSelection();
    }
}