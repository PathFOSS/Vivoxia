package com.pathfoss.vivoxia.general;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.textfield.TextInputEditText;
import com.pathfoss.vivoxia.R;

import java.util.Objects;

public class ReusableMethods {

    // Create method to set scrollable layout listener to change top bar color
    public static void setScrollViewListener(@NonNull NestedScrollView nestedScrollView, ViewChangeListener viewChangeListener) {
        nestedScrollView.findViewById(R.id.ncv).setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (oldScrollY == 0 && scrollY > 0) {
                viewChangeListener.onScrollDown(true);
            } else if (scrollY == 0 && oldScrollY > 0) {
                viewChangeListener.onScrollDown(false);
            }
        });
    }

    // Create method to set return user input from a TextInputEditText as float
    public static float getTextInputEditTextValue(TextInputEditText et) {
        try {
            return Float.parseFloat(Objects.requireNonNull(et.getText()).toString());
        } catch (Exception e) {
            return 0f;
        }
    }

    // Create method to set window parameter for DialogFragments
    public static void setDialogWindowParameters(@NonNull Dialog dialog) {

        Window window = dialog.getWindow();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(window).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.dimAmount = 0.7f;
        window.setAttributes(layoutParams);

        window.setBackgroundDrawable(new InsetDrawable(new ColorDrawable(Color.TRANSPARENT), Units.convertDPtoPX(20)));
    }

    // Create method to graphically set up AutoCompleteTextViews
    @NonNull
    public static AutoCompleteTextView setAutoCompleteTextViewParameters(Context context, @NonNull AutoCompleteTextView autoCompleteTextView, String[] stringList, int defaultIndex) {
        autoCompleteTextView.setDropDownBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.background_dropdown));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.autocomplete_list_item, stringList);
        arrayAdapter.setDropDownViewResource(R.layout.autocomplete_list_item);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setText(stringList[defaultIndex], false);
        return autoCompleteTextView;
    }

    // Create method to graphically set up AutoCompleteTextViews
    public static void setScrollChangeListener(@NonNull NestedScrollView nestedScrollView, ViewChangeListener viewChangeListener) {
        nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (oldScrollY == 0 && scrollY > 0) {
                viewChangeListener.onScrollDown(true);
            } else if (scrollY == 0 && oldScrollY > 0) {
                viewChangeListener.onScrollDown(false);
            }
        });
    }

    // Create method to change text color in journal entries
    public static void colorJournalTextViews (@NonNull TextView tvLeft, @NonNull TextView tvRight, int colorInt) {
        tvLeft.setTextColor(colorInt);
        tvRight.setTextColor(colorInt);
    }
}