package com.pathfoss.vivoxia.exercise;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.EntryAddedListener;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.TopBarListener;
import com.pathfoss.vivoxia.general.ViewChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExerciseSelector extends DialogFragment {

    private static Exercise selectedExercise;

    private final TopBarListener topBarListener;
    private final EntryAddedListener entryAddedListener;

    public ExerciseSelector (TopBarListener topBarListener, EntryAddedListener entryAddedListener) {
        this.topBarListener = topBarListener;
        this.entryAddedListener = entryAddedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate, define, and modify views
        View view = getLayoutInflater().inflate(R.layout.searchable_list, container, false);

        ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.at_select_exercise));
        TextInputEditText searchBar = view.findViewById(R.id.til).findViewById(R.id.met);
        ListView exerciseListView = view.findViewById(R.id.lv);
        
        // Create the exercise list
        List<String> exerciseList = new ArrayList<>();
        for (Exercise exercise : Controller.getExerciseDataBase().getExerciseList()) {
            exerciseList.add(exercise.getName());
        }

        // Set searchable adapter to list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_view_item, exerciseList);
        exerciseListView.setAdapter(adapter);

        // Change result according to user input in search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { adapter.getFilter().filter(s);}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set click listeners for individual exercise items
        exerciseListView.setOnItemClickListener((parent, viewContainer, position, id) -> {
            selectedExercise = Controller.getExerciseDataBase().getExercise(exerciseList.get(position));
            new ExerciseAdder(entryAddedListener).show(getParentFragmentManager(), "Exercise Adder");
            dismiss();
        });

        // Set click listener to create new exercise
        view.findViewById(R.id.ib_add).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fcv_main, new ExerciseCreator((ViewChangeListener) requireContext())).commit();
            topBarListener.textChanged("Create Exercise");
            Controller.changeGroupIDs(205);
            dismiss();
        });

        return view;
    }

    // Create method to set the window parameters
    @Override
    public void onResume() {
        super.onResume();
        ReusableMethods.setDialogWindowParameters(Objects.requireNonNull(getDialog()));
    }

    // Create getter method for the exercise adder
    public static Exercise getSelectedExercise() {
        return selectedExercise;
    }
}