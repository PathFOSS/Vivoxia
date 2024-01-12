package com.pathfoss.vivoxia.nutrition;

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
import java.util.Objects;

public class FoodSelector extends DialogFragment {

    private static Food selectedFood;

    private final TopBarListener topBarListener;
    private final EntryAddedListener entryAddedListener;

    public FoodSelector (TopBarListener topBarListener, EntryAddedListener entryAddedListener) {
            this.topBarListener = topBarListener;
            this.entryAddedListener = entryAddedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate, define, and modify views
        View view = getLayoutInflater().inflate(R.layout.searchable_list, container, false);

        ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.at_select_food));
        TextInputEditText searchBar = view.findViewById(R.id.til).findViewById(R.id.met);
        ListView foodListView = view.findViewById(R.id.lv);

        // Set searchable adapter to list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_view_item, Controller.foodList);
        foodListView.setAdapter(adapter);

        // Change result according to user input in search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start >= 2) {
                    adapter.getFilter().filter(s);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set click listeners for individual food items
        foodListView.setOnItemClickListener((parent, viewContainer, position, id) -> {
            selectedFood = Controller.getFoodDataBase().getEntry((String) ((TextView) viewContainer).getText());
            new FoodAdder(entryAddedListener).show(getParentFragmentManager(), "Food Adder");
            dismiss();
        });

        // Set click listener to import foods from online source
        view.findViewById(R.id.ib_download).setOnClickListener(v -> {
            new FoodImporter().show(getParentFragmentManager(), "Food Importer");
            dismiss();
        });

        // Set click listener to create new food
        view.findViewById(R.id.ib_add).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fcv_main, new FoodCreator()).commit();
            topBarListener.textChanged("Create Food");
            Controller.changeGroupIDs(105);
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

    // Create getter method for the food adder
    public static Food getselectedFood() {
        return selectedFood;
    }
}