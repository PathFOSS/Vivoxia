package com.pathfoss.vivoxia.nutrition;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.SliderFormatter;

import java.util.HashMap;
import java.util.Objects;

public class FoodGoals extends Fragment {

    private Object[][] objects;
    private ImageButton btnSave;
    private Slider caloriesSlider;
    private Slider proteinSlider;
    private Slider fatSlider;
    private Slider carbsSlider;

    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();
    private final SharedPreferences.Editor editor = Controller.getSharedPreferencesEditor();
    private final HashMap<Slider, Boolean> sliderEnableMap = new HashMap<>();
    private final HashMap<Boolean, ColorStateList> colorMap = new HashMap<>();

    private int oldCalories;
    private int currentSliderIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorMap.put(false, AppCompatResources.getColorStateList(requireContext(), R.color.light_gray));
        colorMap.put(true, AppCompatResources.getColorStateList(requireContext(), R.color.vivoxia));
        oldCalories = (int) sharedPreferences.getFloat("Calories", 0f);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflate, define, and modify views
        View view = inflater.inflate(R.layout.fragment_food_goals, container, false);

        btnSave = view.findViewById(R.id.ib_save);

        ConstraintLayout clCalories = view.findViewById(R.id.cl_calories);
        ConstraintLayout clProtein = view.findViewById(R.id.cl_protein);
        ConstraintLayout clFat = view.findViewById(R.id.cl_fat);
        ConstraintLayout clCarbs = view.findViewById(R.id.cl_carbs);

        caloriesSlider = clCalories.findViewById(R.id.slider);
        proteinSlider = clProtein.findViewById(R.id.slider);
        fatSlider = clFat.findViewById(R.id.slider);
        carbsSlider = clCarbs.findViewById(R.id.slider);
        
        objects = new Object[][]{
                {caloriesSlider, clCalories.findViewById(R.id.tv_title), clCalories.findViewById(R.id.tv_value), getString(R.string.calories_capitalized), 1, "kcal"},
                {proteinSlider, clProtein.findViewById(R.id.tv_title), clProtein.findViewById(R.id.tv_value), getString(R.string.protein_capitalized), 4, "g"},
                {fatSlider, clFat.findViewById(R.id.tv_title), clFat.findViewById(R.id.tv_value), getString(R.string.fat_capitalized), 9, "g"},
                {carbsSlider, clCarbs.findViewById(R.id.tv_title), clCarbs.findViewById(R.id.tv_value), getString(R.string.carbs_capitalized), 4, "g"}
        };
        
        for (Object[] object : objects) {
            sliderEnableMap.put((Slider) object[0], true);
        }
        
        // Set listener to only allow for one slider modification at a time
        view.findViewById(R.id.ib_down).setOnClickListener( v -> {
            currentSliderIndex++;

            if (currentSliderIndex % 4 == 2) {
                processCaloriesSlider();
            }
            limitSliderValues();

            boolean truth = Boolean.TRUE.equals(sliderEnableMap.get((Slider) objects[(currentSliderIndex - 1) % 4][0]));

            if (currentSliderIndex - 2 >= 0) {
                toggleSliderEnabled((Slider) objects[(currentSliderIndex - 2) % 4][0], false);
            }

            while (!truth) {
                Toast.makeText(requireContext(), objects[(currentSliderIndex - 1) % 4][3] + " has reached maximum value", Toast.LENGTH_SHORT).show();
                currentSliderIndex++;
                truth = Boolean.TRUE.equals(sliderEnableMap.get((Slider) objects[(currentSliderIndex - 1) % 4][0]));
            }

            Slider newSlider = (Slider) objects[(currentSliderIndex - 1) % 4][0];
            toggleSliderEnabled(newSlider, Boolean.TRUE.equals(sliderEnableMap.get(newSlider)));
            setSliderListener(newSlider, (TextView) objects[(currentSliderIndex - 1) % 4][2], (String) objects[(currentSliderIndex - 1) % 4][5]);
        });

        // Refresh the view with up to date values 
        drawOldValues();
        setConfirmListener();
        
        return view;
    }

    // Create method to prevent mismatched macro values when calories value is changed
    private void processCaloriesSlider () {
        if (oldCalories < (int) caloriesSlider.getValue()) {
            limitSliderValues();
        } else if (oldCalories > (int) caloriesSlider.getValue()) {
            for (int i = 1; i <= 3; i++) {
                ((Slider) objects[i][0]).setValue(0f);
                String sliderText = 0 + " " + objects[i][5];
                ((TextView) objects[i][2]).setText(sliderText);
            }
        }
        oldCalories = (int) caloriesSlider.getValue();
    }

    // Create method to set maximum values based on calories value
    private void limitSliderValues () {

        int calories = (int) caloriesSlider.getValue();
        int caloriesUsed = 0;

        for (int i = 1; i <= 3; i++) {
            caloriesUsed += ((int) ((Slider) objects[i][0]).getValue()) * (int) objects[i][4];
        }

        int caloriesLeft = calories - caloriesUsed;

        for (int i = 1; i <= 3; i++) {
            Slider slider = (Slider) objects[i][0];
            sliderEnableMap.put(slider, true);

            int maxLeft = caloriesLeft / ((int) objects[i][4]);

            if ((int) slider.getValue() + maxLeft == 0) {
                sliderEnableMap.put(slider, false);
            } else {
                slider.setValueTo((int) slider.getValue() + maxLeft);
            }
        }
    }

    // Create method to redraw old goal values
    private void drawOldValues () {
        for (Object[] object : objects) {
            Slider slider = (Slider) object[0];
            String title = (String) object[3];
            int value = (int) sharedPreferences.getFloat(title, 0);
            slider.setValueTo(Math.max(2000f, sharedPreferences.getFloat(title, 0) + 100f));
            slider.setValue(value);
            toggleSliderEnabled(slider, false);
            ((TextView) object[1]).setText(title);
            String valueText = value + " " + object[5];
            ((TextView) object[2]).setText(valueText);
        }
    }

    // Create method to crete listener for each Slider
    private void setSliderListener (@NonNull Slider slider, TextView textView, String dataType) {
        slider.setLabelFormatter(new SliderFormatter(dataType));

        slider.addOnChangeListener((slider1, value, fromUser) -> {
            String valueText = (int) value + " " + dataType;
            textView.setText(valueText);

            if (slider1.equals(caloriesSlider) && value == slider1.getValueTo()) {
                slider.setValueTo(value + 10);
            }

            setConfirmListener();
        });
    }

    // Create method to toggle sliders on and off
    private void toggleSliderEnabled (@NonNull Slider slider, boolean enabled) {
        slider.setEnabled(enabled);
        slider.setTrackActiveTintList(Objects.requireNonNull(colorMap.get(enabled)));
        slider.setThumbTintList(Objects.requireNonNull(colorMap.get(enabled)));
    }

    // Create method to toggle sliders on and off
    private void setConfirmListener() {
        if (getCaloriesLeft() <= 3 && getCaloriesLeft() >= 0) {
            btnSave.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_save));
            btnSave.setOnClickListener( v -> {
                setSharedPreferences();
                Toast.makeText(getContext(), "Targets saved", Toast.LENGTH_SHORT).show();
            });
        } else {
            btnSave.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_save_disabled));
            btnSave.setOnClickListener(null);
        }
    }

    // Create method to set SharedPreferences
    private void setSharedPreferences() {
        editor.putFloat(getString(R.string.calories_capitalized), caloriesSlider.getValue()).apply();
        editor.putFloat(getString(R.string.protein_capitalized), proteinSlider.getValue()).apply();
        editor.putFloat(getString(R.string.fat_capitalized), fatSlider.getValue()).apply();
        editor.putFloat(getString(R.string.carbs_capitalized), carbsSlider.getValue()).apply();
    }

    // Create method to compute and return the total number of calories derived from macronutrients
    private int getCaloriesLeft() {
        return (int) (caloriesSlider.getValue() - proteinSlider.getValue() * 4 - fatSlider.getValue() * 9 - carbsSlider.getValue() * 4);
    }

    // Create method to redraw all views to reflect up to date values
    @Override
    public void onResume() {
        drawOldValues();
        super.onResume();
    }
}