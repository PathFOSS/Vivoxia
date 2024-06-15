package com.pathfoss.vivoxia.exercise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.EntryAddedListener;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.SelectorClickedListener;
import com.pathfoss.vivoxia.general.Units;

import java.util.Objects;

public class ExerciseAdder extends DialogFragment {

    private final String [] units = {"0 / 10", "1 / 10", "2 / 10", "3 / 10", "4 / 10", "5 / 10", "6 / 10", "7 / 10", "8 / 10", "9 / 10", "10 / 10"};
    private final EntryAddedListener entryAddedListener;
    private final SelectorClickedListener selectorClickedListener;
    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();

    private int unitIndex = 0;

    public ExerciseAdder (EntryAddedListener entryAddedListener, SelectorClickedListener selectorClickedListener) {
        this.entryAddedListener = entryAddedListener;
        this.selectorClickedListener = selectorClickedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate, define, and modify views
        View view = inflater.inflate(R.layout.fragment_exercise_adder, container, false);

        Button btnAdd = view.findViewById(R.id.b_add);
        TextView tvTitle = view.findViewById(R.id.tv_title);

        Exercise exercise = ExerciseSelector.getSelectedExercise();
        tvTitle.setText(exercise.getName());

        ConstraintLayout clRepsWeight = view.findViewById(R.id.cl_reps_weight);
        ConstraintLayout clRestWork = view.findViewById(R.id.cl_rest_work);

        TextInputLayout tilSets = view.findViewById(R.id.til_sets);
        TextInputLayout tilReps = clRepsWeight.findViewById(R.id.til_left);
        TextInputLayout tilWeight = clRepsWeight.findViewById(R.id.til_right);
        TextInputLayout tilRest = clRestWork.findViewById(R.id.til_left);
        TextInputLayout tilWork = clRestWork.findViewById(R.id.til_right);
        TextInputLayout tilEffort = view.findViewById(R.id.til_effort);

        tilSets.setHint("Sets");
        tilReps.setHint("Reps");
        tilWeight.setHint("Weight");
        tilRest.setHint("Rest");
        tilWork.setHint("Work");
        tilEffort.setHint("Effort");

        TextInputEditText sets = tilSets.findViewById(R.id.met);
        TextInputEditText reps = tilReps.findViewById(R.id.met);
        TextInputEditText weight = tilWeight.findViewById(R.id.met);
        TextInputEditText rest = tilRest.findViewById(R.id.met);
        TextInputEditText work = tilWork.findViewById(R.id.met);

        // Set on click listeners
        AutoCompleteTextView act = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), view.findViewById(R.id.act), units, unitIndex);
        act.setOnItemClickListener((parent, view1, position, id) -> unitIndex = position);
        act.setOnClickListener(v -> {
            selectorClickedListener.selectorClicked(view.findFocus());
        });

        // Set autofill parameters
        sets.setText(String.valueOf(sharedPreferences.getInt("Sets_Autofill", 1)));
        reps.setText(String.valueOf(sharedPreferences.getInt("Reps_Autofill", 0)));
        rest.setText(String.valueOf(sharedPreferences.getInt("Rest_Autofill", 40)));
        work.setText(String.valueOf(sharedPreferences.getInt("Work_Autofill", 40)));
        act.setText(units[sharedPreferences.getInt("Effort_Autofill", 0)], false);

        btnAdd.setOnClickListener( v -> {

            // Add entry to database given non-zero sets
            if (ReusableMethods.getTextInputEditTextValue(sets) > 0) {
                Controller.getExerciseJournalDataBase().addEntry(
                        ExerciseJournal.getMeasurementDate(),
                        exercise.getName(),
                        ReusableMethods.getTextInputEditTextValue(sets),
                        ReusableMethods.getTextInputEditTextValue(reps),
                        Units.toMetric(ReusableMethods.getTextInputEditTextValue(weight), Units.getMassUnit()),
                        ReusableMethods.getTextInputEditTextValue(rest),
                        ReusableMethods.getTextInputEditTextValue(work),
                        unitIndex
                );
                entryAddedListener.onEntryAdded();
                Toast.makeText(requireContext(), (int) ReusableMethods.getTextInputEditTextValue(sets) + " sets of " + exercise.getName() + " added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Sets cannot be zero", Toast.LENGTH_SHORT).show();
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