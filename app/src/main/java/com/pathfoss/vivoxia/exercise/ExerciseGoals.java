package com.pathfoss.vivoxia.exercise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.SliderFormatter;
import com.pathfoss.vivoxia.general.Units;
import com.pathfoss.vivoxia.general.ViewChangeListener;

import java.util.Objects;

public class ExerciseGoals extends Fragment {

    private View view;
    private Object[][] setList;
    private Slider volumeSlider;

    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();
    private final SharedPreferences.Editor sharedPreferencesEditor = Controller.getSharedPreferencesEditor();

    private final ViewChangeListener viewChangeListener;

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

    // Create a constructor for setting UI change listener
    public ExerciseGoals (ViewChangeListener viewChangeListener) {
        this.viewChangeListener = viewChangeListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate, define, and modify views
        view = inflater.inflate(R.layout.fragment_exercise_goals, container, false);
        ReusableMethods.setScrollChangeListener(view.findViewById(R.id.ncv), viewChangeListener);

        ConstraintLayout clWorkouts = view.findViewById(R.id.sl_workouts);
        ConstraintLayout clVolume = view.findViewById(R.id.sl_volume);
        ConstraintLayout clReps = view.findViewById(R.id.sl_reps);
        ConstraintLayout clSets = view.findViewById(R.id.sl_sets);
        ConstraintLayout clDuration = view.findViewById(R.id.sl_duration);
        ConstraintLayout clRest = view.findViewById(R.id.sl_rest);
        ConstraintLayout clWork = view.findViewById(R.id.sl_work);
        ConstraintLayout clEffort = view.findViewById(R.id.sl_effort);

        volumeSlider = clVolume.findViewById(R.id.slider);

        setList = new Object[][]{
                {"Workouts", "/ week", clWorkouts.findViewById(R.id.tv_title), clWorkouts.findViewById(R.id.tv_value), clWorkouts.findViewById(R.id.slider)},
                {"Volume", Units.getMassUnit(), clVolume.findViewById(R.id.tv_title), clVolume.findViewById(R.id.tv_value), volumeSlider},
                {"Reps", "/ set", clReps.findViewById(R.id.tv_title), clReps.findViewById(R.id.tv_value), clReps.findViewById(R.id.slider)},
                {"Sets", "/ day", clSets.findViewById(R.id.tv_title), clSets.findViewById(R.id.tv_value), clSets.findViewById(R.id.slider)},
                {"Duration", "min", clDuration.findViewById(R.id.tv_title), clDuration.findViewById(R.id.tv_value), clDuration.findViewById(R.id.slider)},
                {"Work", "s", clWork.findViewById(R.id.tv_title), clWork.findViewById(R.id.tv_value), clWork.findViewById(R.id.slider)},
                {"Rest", "s", clRest.findViewById(R.id.tv_title), clRest.findViewById(R.id.tv_value), clRest.findViewById(R.id.slider)},
                {"Effort", "/ 10", clEffort.findViewById(R.id.tv_title), clEffort.findViewById(R.id.tv_value), clEffort.findViewById(R.id.slider)}
        };

        volumeSlider.setStepSize(10f);
        updateSliders();
        return view;
    }

    // Create method to update sliders to current values
    private void updateSliders() {

        // Set all sliders to old values
        for (int i = 0; i < setList.length; i++) {

            // Cast reusable objects
            Object[] objects = setList[i];
            String unit = (String) objects[1];
            float currentValue = sharedPreferences.getFloat(exerciseMeasureList[i], 0f);
            TextView textView = ((TextView) objects[3]);
            Slider slider = (Slider) objects[4];
            ((TextView) objects[2]).setText((CharSequence) objects[0]);

            // Define slider element parameters
            if (!Objects.equals(objects[1], "/ 10")) {
                slider.setValueTo((int) currentValue + 10);
            } else {
                slider.setValueTo(10);
            }

            if (i == 1) {
                float massValue = (float) ((int) Math.round(Units.fromMetric(currentValue, Units.getMassUnit()) / 10.0) * 10);
                String massText = (int) massValue + " " + Units.getMassUnit();
                slider.setValueTo(massValue + 200);
                slider.setValue(massValue);
                textView.setText(massText);
            } else {
                textView.setText(Units.toHeightIfApplicable((int) currentValue, unit));
                slider.setValue((float) ((int) currentValue));
            }

            setSliderListener(slider, textView, unit);
        }

        // Notify user and save values on click
        view.findViewById(R.id.ib_save).setOnClickListener( v -> {
            for (int i = 0; i < setList.length; i++) {
                if (i == 1) {
                    sharedPreferencesEditor.putFloat(exerciseMeasureList[i], Units.toMetric(((Slider) setList[i][4]).getValue(), Units.getMassUnit())).apply();
                } else {
                    sharedPreferencesEditor.putFloat(exerciseMeasureList[i], ((Slider) setList[i][4]).getValue()).apply();
                }
            }
            Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show();
        });

        // Set scroll listener to change top bar color
        ReusableMethods.setScrollViewListener(view.findViewById(R.id.ncv), viewChangeListener);
    }

    // Create method to set listener to a slider
    private void setSliderListener (@NonNull Slider slider, TextView textView, String unit) {

        // Set slider formatter
        slider.setLabelFormatter(new SliderFormatter(unit));

        // Set slider listener
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            textView.setText(Units.toHeightIfApplicable((int) value, unit));
            if (!Objects.equals(unit, "/ 10") && value == slider1.getValueTo()) {
                if (slider.equals(volumeSlider)) {
                    slider1.setValueTo(value + 10);
                } else {
                    slider1.setValueTo(value + 1);
                }
            }
        });
    }

    // Create method to redraw sliders on user resume
    @Override
    public void onResume() {
        super.onResume();
         updateSliders();
    }
}