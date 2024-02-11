package com.pathfoss.vivoxia.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.body.BodyDashboard;
import com.pathfoss.vivoxia.body.BodyGoals;
import com.pathfoss.vivoxia.body.BodyJournal;
import com.pathfoss.vivoxia.body.BodyJournalDataBase;
import com.pathfoss.vivoxia.exercise.ExerciseCreator;
import com.pathfoss.vivoxia.exercise.ExerciseDashboard;
import com.pathfoss.vivoxia.exercise.ExerciseDataBase;
import com.pathfoss.vivoxia.exercise.ExerciseGoals;
import com.pathfoss.vivoxia.exercise.ExerciseJournal;
import com.pathfoss.vivoxia.exercise.ExerciseJournalDataBase;
import com.pathfoss.vivoxia.exercise.ExerciseTimer;
import com.pathfoss.vivoxia.nutrition.Food;
import com.pathfoss.vivoxia.nutrition.FoodCreator;
import com.pathfoss.vivoxia.nutrition.FoodDashboard;
import com.pathfoss.vivoxia.nutrition.FoodDataBase;
import com.pathfoss.vivoxia.nutrition.FoodEstimator;
import com.pathfoss.vivoxia.nutrition.FoodGoals;
import com.pathfoss.vivoxia.nutrition.FoodJournal;
import com.pathfoss.vivoxia.nutrition.FoodJournalDataBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressLint("SimpleDateFormat")
public class Controller extends AppCompatActivity implements ViewChangeListener, TopBarListener, DialogListener {
    
    private static FoodDataBase foodDataBase;
    private static FoodJournalDataBase foodJournalDataBase;
    private static BodyJournalDataBase bodyJournalDataBase;
    private static ExerciseDataBase exerciseDataBase;
    private static ExerciseJournalDataBase exerciseJournalDataBase;

    private static int colorVivoxia;
    private static int colorVivoxiaBackground;
    private static int colorVivoxiaForeground;
    private static int colorNaturalWhite;

    private static int previousPreviousGroup = 200;
    private static int previousGroup = 200;
    private static int currentGroup = 200;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor sharedPreferencesEditor;
    
    private static final SimpleDateFormat dataBaseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat topBarDateFormat = new SimpleDateFormat("MMM d, yyyy");
    private final HashMap<Integer, Object[][]> menuGroupMap = new HashMap<>();

    private final HashMap<ConstraintLayout, Object[]> objects = new HashMap<>();
    private final HashMap<Integer, Pair<Object, String>> fragmentMenuMap = new HashMap<>();
    private final HashMap<Boolean, Integer> colorMap = new HashMap<>();

    private LinearLayout llArrow;
    private LinearLayout llSettings;
    private ConstraintLayout clDiet;
    private ConstraintLayout clExercise;
    private ConstraintLayout clBody;
    private ConstraintLayout clBottomBar;
    private TextView tvTitle;
    private ListPopupWindow listPopupWindow;

    private boolean popupWindowVisible = false;

    public static List<String> foodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize SharedPreferences objects
        sharedPreferences = getSharedPreferences("General", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        // Initialize database objects
        foodDataBase = new FoodDataBase(this);
        foodJournalDataBase = new FoodJournalDataBase(this);
        bodyJournalDataBase = new BodyJournalDataBase(this);
        exerciseDataBase = new ExerciseDataBase(this);
        exerciseJournalDataBase = new ExerciseJournalDataBase(this);

        // Initialize colors
        colorVivoxia = getColor(R.color.vivoxia);
        colorVivoxiaBackground = getColor(R.color.vivoxia_background);
        colorVivoxiaForeground = getColor(R.color.vivoxia_foreground);
        colorNaturalWhite = getColor(R.color.natural_white);
        colorMap.put(false, R.color.vivoxia_background);
        colorMap.put(true, R.color.vivoxia_foreground);

        // Set view and initialize layout
        setContentView(R.layout.activity_controller);

        llArrow = findViewById(R.id.ll_arrow);
        tvTitle = findViewById(R.id.tv_title);
        llSettings = findViewById(R.id.ll_settings);
        
        clBottomBar = findViewById(R.id.cl_bottom_bar);

        clDiet = findViewById(R.id.cl_diet);
        clExercise = findViewById(R.id.cl_exercise);
        clBody = findViewById(R.id.cl_body);
        
        objects.put(clDiet, new Object[]{clBottomBar.findViewById(R.id.iv_diet), R.drawable.icon_diet_background, R.drawable.icon_diet_highlight});
        objects.put(clExercise, new Object[]{clBottomBar.findViewById(R.id.iv_exercise), R.drawable.icon_exercise_background, R.drawable.icon_exercise_highlight});
        objects.put(clBody, new Object[]{clBottomBar.findViewById(R.id.iv_body), R.drawable.icon_body_background, R.drawable.icon_body_highlight});

        toggleBottomBarHighlights(clExercise);
        
        // Set DPI measure for conversions from pixels
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Units.phoneDPI = metrics.densityDpi;
        
        // Initialize menu and listeners
        setBottomBarListeners();
        initializeGlobalMenu();

        llSettings.setOnClickListener(v -> {
            if (popupWindowVisible) {
                listPopupWindow.dismiss();
            } else {
                setMenuListener();
                listPopupWindow.show();
            }
            popupWindowVisible = !popupWindowVisible;
        });

        cacheFood();
    }

    // Create method to create a new menu items group array
    @NonNull
    private List<String> getMenuArray () {
        List<String> menuGroup = new ArrayList<>();
        for (Object[] objectList : getFromGroupMap(currentGroup)) {
            menuGroup.add((String) objectList[1]);
        }
        return menuGroup;
    }

    // Create method to create global menu for user navigation between fragments
    private void initializeGlobalMenu () {

        listPopupWindow = new ListPopupWindow(new ContextThemeWrapper(this, R.style.vivoxia_popup_menu));

        listPopupWindow.setAnchorView(llSettings);
        listPopupWindow.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.background_dropdown));
        listPopupWindow.setWidth(Units.convertDPtoPX(150));
        listPopupWindow.setVerticalOffset(0);
        listPopupWindow.setHorizontalOffset(-Units.convertDPtoPX(110));

        fragmentMenuMap.put(0, new Pair<>(new Settings(), "Settings"));
        String today = topBarDateFormat.format(System.currentTimeMillis());
        Context context = this;

        menuGroupMap.put(100, new Object[][]{{101, "Journal"}, {102, "Set Goals"}, {103, "Edit Chart"}, {0, "Settings"}});
        menuGroupMap.put(101, new Object[][]{{104, "Change Date"}, {0, "Settings"}});
        menuGroupMap.put(102, new Object[][]{{106, "Calculator"}, {0, "Settings"}});
        menuGroupMap.put(105, new Object[][]{{0, "Settings"}});
        menuGroupMap.put(106, new Object[][]{{0, "Settings"}});

        fragmentMenuMap.put(100, new Pair<>(new FoodDashboard(), sharedPreferences.getString("FoodChartItem", "Calories") + " (" + Units.getFoodDataType() + ")"));
        fragmentMenuMap.put(101, new Pair<>(new FoodJournal(this, this), today));
        fragmentMenuMap.put(102, new Pair<>(new FoodGoals(), "Set Goals"));
        fragmentMenuMap.put(103, new Pair<>(new EditChart(this, "FoodChartItem", "Calories"), ""));
        fragmentMenuMap.put(104, new Pair<>(new EditDate(context, (DateChangeListener) Objects.requireNonNull(fragmentMenuMap.get(101)).first, System.currentTimeMillis()), ""));
        fragmentMenuMap.put(105, new Pair<>(new FoodCreator(), "Create Food"));
        fragmentMenuMap.put(106, new Pair<>(new FoodEstimator(), "Calorie Needs"));

        menuGroupMap.put(200, new Object[][]{{201, "Journal"}, {202, "Set Goals"}, {203, "Edit Chart"}, {0, "Settings"}});
        menuGroupMap.put(201, new Object[][]{{204, "Change Date"}, {0, "Settings"}});
        menuGroupMap.put(202, new Object[][]{{0, "Settings"}});
        menuGroupMap.put(205, new Object[][]{{0, "Settings"}});
        menuGroupMap.put(206, new Object[][]{{0, "Settings"}});

        fragmentMenuMap.put(200, new Pair<>(new ExerciseDashboard(), sharedPreferences.getString("ExerciseChartItem", "Volume / Workout") + " (" + Units.getExerciseDataType() + ")"));
        fragmentMenuMap.put(201, new Pair<>(new ExerciseJournal(this, this), today));
        fragmentMenuMap.put(202, new Pair<>(new ExerciseGoals(this), "Set Goals"));
        fragmentMenuMap.put(203, new Pair<>(new EditChart(this, "ExerciseChartItem", "Volume / Workout"), ""));
        fragmentMenuMap.put(204, new Pair<>(new EditDate(context, (DateChangeListener) Objects.requireNonNull(fragmentMenuMap.get(201)).first, System.currentTimeMillis()), ""));
        fragmentMenuMap.put(205, new Pair<>(new ExerciseCreator(this), "Create Exercise"));
        fragmentMenuMap.put(206, new Pair<>(new ExerciseTimer(this), ""));

        menuGroupMap.put(300, new Object[][]{{301, "Journal"}, {302, "Set Goals"}, {303, "Edit Chart"}, {0, "Settings"}});
        menuGroupMap.put(301, new Object[][]{{304, "Change Date"}, {0, "Settings"}});
        menuGroupMap.put(302, new Object[][]{{0, "Settings"}});

        fragmentMenuMap.put(300, new Pair<>(new BodyDashboard(), sharedPreferences.getString("BodyChartItem", "Weight") + " (" + Units.getBodyDataType() + ")"));
        fragmentMenuMap.put(301, new Pair<>(new BodyJournal(this), today));
        fragmentMenuMap.put(302, new Pair<>(new BodyGoals(this), "Set Goals"));
        fragmentMenuMap.put(303, new Pair<>(new EditChart(this, "BodyChartItem", "Weight"), ""));
        fragmentMenuMap.put(304, new Pair<>(new EditDate(context, (DateChangeListener) Objects.requireNonNull(fragmentMenuMap.get(301)).first, System.currentTimeMillis()), ""));

        tvTitle.setText(Objects.requireNonNull(fragmentMenuMap.get(200)).second);
        listPopupWindow.setAdapter(new ArrayAdapter<>(this, R.layout.autocomplete_list_item, getMenuArray()));
    }

    // Create method to set menu item listeners to swap between fragments
    private void setMenuListener() {
        listPopupWindow.setAdapter(new ArrayAdapter<>(this, R.layout.autocomplete_list_item, getMenuArray()));

        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {

            int ID = (int) Objects.requireNonNull(menuGroupMap.get(currentGroup))[position][0];

            Object object = Objects.requireNonNull(fragmentMenuMap.get(ID)).first;

            if (object instanceof DialogFragment) {
                ((DialogFragment) object).show(getSupportFragmentManager(), String.valueOf(currentGroup));
            } else {
                changeGroupIDs(ID);
                assert object != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, (Fragment) object).commit();
                tvTitle.setText(Objects.requireNonNull(fragmentMenuMap.get(ID)).second);
                setNavigationBackgrounds(false);
                clBottomBar.setVisibility(View.GONE);
                getWindow().setNavigationBarColor(colorVivoxiaBackground);
                setBackButtonListener();
                if (ID == 0) {
                    llSettings.setVisibility(View.GONE);
                }
            }

            // Invalidate the listener
            listPopupWindow.dismiss();
            listPopupWindow.setOnItemSelectedListener(null);
        });
    }

    // Create method to change menu group IDs from anywhere
    public static void changeGroupIDs (int currentID) {
        previousPreviousGroup = previousGroup;
        previousGroup = currentGroup;
        currentGroup = currentID;
    }

    // Create method to set the top bar menu backgrounds
    private void setNavigationBackgrounds (boolean highlighted) {
        int color = getFromColorMap(highlighted);
        llArrow.setBackgroundResource(color);
        llSettings.setBackgroundResource(color);
        tvTitle.setBackgroundResource(color);
        getWindow().setStatusBarColor(getColor(color));
    }

    // Create method to set the back arrow to swap to previous Fragments
    private void setBackButtonListener () {
        llArrow.setVisibility(View.VISIBLE);
        tvTitle.setPaddingRelative(0, 0, Units.convertDPtoPX(20), 0);
        llArrow.setOnClickListener( v -> backButtonClicked());
    }

    // Create method to use Android system back button to function as back arrow

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (previousGroup != 0) {
            backButtonClicked();
        }
    }

    // Create method to standardize action on user return intent
    private void backButtonClicked () {

        if (currentGroup == 0) {
            llSettings.setVisibility(View.VISIBLE);
        }

        currentGroup = previousGroup;
        previousGroup = previousPreviousGroup;
        previousPreviousGroup = 0;

        if (currentGroup == 100 || currentGroup == 200 || currentGroup == 300) {
            tvTitle.setPaddingRelative(Units.convertDPtoPX(20), 0, Units.convertDPtoPX(20), 0);
            if (currentGroup == 100) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, (new FoodDashboard())).commit();
                String titleText = sharedPreferences.getString("FoodChartItem", "Calories") + " (" + Units.getFoodDataType() + ")";
                tvTitle.setText(titleText);
            } else if (currentGroup == 200) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, (new ExerciseDashboard())).commit();
                String titleText = sharedPreferences.getString("ExerciseChartItem", "Volume / Workout") + " (" + Units.getExerciseDataType() + ")";
                if (Units.getExerciseDataType().equals("")) {
                    titleText = sharedPreferences.getString("ExerciseChartItem", "Volume / Workout");
                }
                tvTitle.setText(titleText);
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, new BodyDashboard()).commit();
                String titleText = sharedPreferences.getString("BodyChartItem", "Weight") + " (" + Units.getBodyDataType() + ")";
                tvTitle.setText(titleText);
            }
        } else if (currentGroup == 206) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, (Fragment) Objects.requireNonNull(Objects.requireNonNull(fragmentMenuMap.get(201)).first)).commit();
            tvTitle.setText(Objects.requireNonNull(fragmentMenuMap.get(201)).second);
            currentGroup = 201;
            previousGroup = 200;
        } else if (currentGroup != 0){
            getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, (Fragment) Objects.requireNonNull(Objects.requireNonNull(fragmentMenuMap.get(currentGroup)).first)).commit();
            tvTitle.setText(Objects.requireNonNull(fragmentMenuMap.get(currentGroup)).second);
        }

        checkBottomBarVisibility();
        setNavigationBackgrounds(false);
    }

    // Create method to set the back arrow to swap to previous Fragments
    private void checkBottomBarVisibility () {
        if (currentGroup != 0 && currentGroup % 100 == 0) {
            clBottomBar.setVisibility(View.VISIBLE);
            getWindow().setNavigationBarColor(colorVivoxiaForeground);
            llArrow.setVisibility(View.GONE);
        }
    }

    // Create method to set the back arrow to swap to previous Fragments
    private void setBottomBarListeners() {
        
        clDiet.setOnClickListener( v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, (new FoodDashboard())).commit();
            toggleBottomBarHighlights(clDiet);
            String titleText = sharedPreferences.getString("FoodChartItem", "Calories") + " (" + Units.getFoodDataType() + ")";
            tvTitle.setText(titleText);
            currentGroup = 100;
        });
        
        clExercise.setOnClickListener( v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, new ExerciseDashboard()).commit();
            toggleBottomBarHighlights(clExercise);
            String titleText = sharedPreferences.getString("ExerciseChartItem", "Volume / Workout") + " (" + Units.getExerciseDataType() + ")";
            if (Units.getExerciseDataType().equals("")) {
                titleText = sharedPreferences.getString("ExerciseChartItem", "Volume / Workout");
            }
            tvTitle.setText(titleText);
            currentGroup = 200;
        });
        
        clBody.setOnClickListener( v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fcv_main, new BodyDashboard()).commit();
            toggleBottomBarHighlights(clBody);
            String titleText = sharedPreferences.getString("BodyChartItem", "Weight") + " (" + Units.getBodyDataType() + ")";
            tvTitle.setText(titleText);
            currentGroup = 300;
        });
    }

    // Create method to set a highlighted drawable to selected bottom bar button layout
    public void toggleBottomBarHighlights (ConstraintLayout activeCL) {
        for (ConstraintLayout constraintLayout : objects.keySet()) {
            if (constraintLayout != activeCL) {
                ((ImageView) Objects.requireNonNull(objects.get(constraintLayout))[0]).setBackgroundResource((int) Objects.requireNonNull(objects.get(constraintLayout))[1]);
            } else {
                ((ImageView) Objects.requireNonNull(objects.get(constraintLayout))[0]).setBackgroundResource((int) Objects.requireNonNull(objects.get(constraintLayout))[2]);
            }
        }
    }

    // Create null-safe method to get values from the color map
    @NonNull
    private Integer getFromColorMap(boolean key) {
        Integer output = colorMap.get(key);
        return output != null ? output : 0;
    }

    // Create null-safe method to get values from the group map
    @NonNull
    private Object[][] getFromGroupMap(int key) {
        Object[][] output = menuGroupMap.get(key);
        return output != null ? output : new Object[][]{};
    }

    // Create method to cache a large food list in memory
    private void cacheFood() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (Food food : Controller.getFoodDataBase().getFoodList()) {
                    foodList.add(food.getName());
                }
            }
        };
        thread.start();
    }

    // Create a method to change top bar color through an interface
    @Override
    public void onScrollDown(boolean scrolledDown) {
        setNavigationBackgrounds(scrolledDown);
    }

    // Create a method to change title text through an interface
    @Override
    public void textChanged(String text) {
        tvTitle.setText(text);
    }

    // Create a method to change top bar title and refresh views through an interface
    @Override
    public void onCloseDialog(String title) {
        tvTitle.setText(title);
    }

    // Set getter methods
    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getSharedPreferencesEditor() {
        return sharedPreferencesEditor;
    }

    public static SimpleDateFormat getDataBaseDateFormat() {
        return dataBaseDateFormat;
    }

    public static SimpleDateFormat getTopBarDateFormat() {
        return topBarDateFormat;
    }

    public static FoodDataBase getFoodDataBase() {
        return foodDataBase;
    }

    public static FoodJournalDataBase getFoodJournalDataBase() {
        return foodJournalDataBase;
    }

    public static BodyJournalDataBase getBodyJournalDataBase() {
        return bodyJournalDataBase;
    }

    public static ExerciseJournalDataBase getExerciseJournalDataBase() {
        return exerciseJournalDataBase;
    }

    public static ExerciseDataBase getExerciseDataBase() {
        return exerciseDataBase;
    }

    public static int getColorVivoxia() {
        return colorVivoxia;
    }

    public static int getColorVivoxiaForeground() {
        return colorVivoxiaForeground;
    }

    public static int getColorNaturalWhite() {
        return colorNaturalWhite;
    }
}