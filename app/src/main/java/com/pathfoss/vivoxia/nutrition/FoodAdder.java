package com.pathfoss.vivoxia.nutrition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.EntryAddedListener;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.Units;

import java.util.Objects;

public class FoodAdder extends DialogFragment {

    private final String [] unitList = {"g", "ml", "oz", "lbs"};
    private final EntryAddedListener entryAddedListener;

    private int unitIndex = 0;
    private float denominator;
    private Food food;

    public FoodAdder (EntryAddedListener entryAddedListener) {
        this.entryAddedListener = entryAddedListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        food = FoodSelector.getselectedFood();
        denominator = Units.toGrams(food.getPortionSize(), food.getMassUnit());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate, define, and modify views
        View view = inflater.inflate(R.layout.fragment_food_adder, container, false);

        TextInputLayout tilAmount = view.findViewById(R.id.til_amount);
        TextInputLayout tilUnit = view.findViewById(R.id.til_unit);
        TextInputEditText metAmount = tilAmount.findViewById(R.id.met);

        tilAmount.setHint("Amount");
        tilUnit.setHint("Unit");

        ((TextView) view.findViewById(R.id.tv_title)).setText(food.getName());

        // Set click listeners
        ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilUnit.findViewById(R.id.act), unitList, 0).setOnItemClickListener((parent, view1, position, id) -> unitIndex = position);

        view.findViewById(R.id.b_add).setOnClickListener( v -> {

            float numerator = Units.toGrams(ReusableMethods.getTextInputEditTextValue(metAmount), unitList[unitIndex]);
            float multiplier = numerator / denominator;

            if (ReusableMethods.getTextInputEditTextValue(metAmount) > 0) {
                Controller.getFoodJournalDataBase().addEntry(FoodJournal.getMeasurementDate(),
                        food.getName(),
                        ReusableMethods.getTextInputEditTextValue(metAmount),
                        unitList[unitIndex],
                        food.getCalories() * multiplier,
                        food.getProtein() * multiplier,
                        food.getFat() * multiplier,
                        food.getCarbs() * multiplier,
                        0f,
                        0f
                );
                entryAddedListener.onEntryAdded();
                Toast.makeText(requireContext(), ReusableMethods.getTextInputEditTextValue(metAmount) + " of " + food.getName() + " added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Amount cannot be zero", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Create method to set the window parameters
    @Override
    public void onResume() {
        super.onResume();
        ReusableMethods.setDialogWindowParameters(Objects.requireNonNull(getDialog()));
    }
}