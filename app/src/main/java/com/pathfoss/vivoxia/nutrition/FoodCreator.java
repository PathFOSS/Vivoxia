package com.pathfoss.vivoxia.nutrition;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.Units;

import java.util.Objects;

public class FoodCreator extends Fragment {

    private final FoodDataBase foodDataBase = Controller.getFoodDataBase();
    private final String [] unitList = {"g", "ml", "oz", "lbs"};
    private int unitIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate, define, and modify views
        View view = inflater.inflate(R.layout.fragment_food_creator, container, false);

        TextInputLayout tilName = view.findViewById(R.id.til_name);
        TextInputLayout tilPortion = view.findViewById(R.id.til_portion);
        TextInputLayout tilUnit = view.findViewById(R.id.til_unit);
        TextInputLayout tilCalories = view.findViewById(R.id.til_calories);
        TextInputLayout tilProtein = view.findViewById(R.id.til_protein);
        TextInputLayout tilFat = view.findViewById(R.id.til_fat);
        TextInputLayout tilCarbs = view.findViewById(R.id.til_carbs);

        tilName.setHint("Name");
        tilPortion.setHint("Portion");
        tilUnit.setHint("Unit");
        tilCalories.setHint("Calories (kcal)");
        tilProtein.setHint("Protein (g)");
        tilFat.setHint("Fat (g)");
        tilCarbs.setHint("Carbs (g)");

        TextInputEditText metName = tilName.findViewById(R.id.met);
        TextInputEditText metPortion = tilPortion.findViewById(R.id.met);
        TextInputEditText metCalories = tilCalories.findViewById(R.id.met);
        TextInputEditText metProtein = tilProtein.findViewById(R.id.met);
        TextInputEditText metFat = tilFat.findViewById(R.id.met);
        TextInputEditText metCarbs = tilCarbs.findViewById(R.id.met);

        metName.setInputType(InputType.TYPE_CLASS_TEXT);

        //Create click listeners
        ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), view.findViewById(R.id.act), unitList, 0).setOnItemClickListener((parent, view1, position, id) -> unitIndex = position);

        view.findViewById(R.id.ib_confirm).setOnClickListener(v -> {
            String name = Objects.requireNonNull(metName.getText()).toString();

            float calories = ReusableMethods.getTextInputEditTextValue(metCalories);
            float portion = ReusableMethods.getTextInputEditTextValue(metPortion);
            float protein = ReusableMethods.getTextInputEditTextValue(metProtein);
            float fat = ReusableMethods.getTextInputEditTextValue(metFat);
            float carbs = ReusableMethods.getTextInputEditTextValue(metCarbs);

                if (!Objects.equals(name, "")) {
                    if (Units.toGrams(portion, unitList[unitIndex]) >= protein + fat + carbs) {
                        if (calories >= protein * 4 + fat * 9 + carbs *4) {
                            Food inputFood = new Food(name,
                                    portion,
                                    unitList[unitIndex],
                                    calories,
                                    protein,
                                    fat,
                                    carbs,
                                    0f,
                                    0f);
                            if (foodDataBase.getEntry(name) == null) {
                                foodDataBase.addEntry(inputFood);
                                Toast.makeText(requireContext(), name + " added", Toast.LENGTH_SHORT).show();
                            } else {
                                foodDataBase.addEntry(inputFood);
                                Toast.makeText(requireContext(), name + " updated", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "There are too few calories for the amount of macronutrients", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Protein, fat, and carbs cannot be more than portion size", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Please specify food name", Toast.LENGTH_SHORT).show();
                }
        });

        return view;
    }
}