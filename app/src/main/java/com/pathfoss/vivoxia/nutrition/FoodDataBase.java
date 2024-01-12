package com.pathfoss.vivoxia.nutrition;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pathfoss.vivoxia.general.Controller;

import java.util.ArrayList;
import java.util.List;

public class FoodDataBase extends SQLiteOpenHelper {

    private final String FOOD_ITEMS_TABLE = "FOOD_ITEMS_TABLE";
    private final String COL_NAME = "NAME";
    private final String COL_PORTION_SIZE = "PORTION_SIZE";
    private final String COL_MASS_UNIT = "MASS_UNIT";
    private final String COL_CALORIES = "CALORIES";
    private final String COL_PROTEIN = "PROTEIN";
    private final String COL_FAT = "FAT";
    private final String COL_CARBS = "CARBS";
    private final String COL_FIBER = "FIBER";
    private final String COL_ALCOHOL = "ALCOHOL";
    
    public FoodDataBase(@Nullable Context context) {super(context, "food_items_table.db", null, 1);}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    @Override
    public void onCreate(@NonNull SQLiteDatabase sqLiteDatabase) {

        // Create table if it does not exist
        sqLiteDatabase.execSQL("CREATE TABLE " 
                + FOOD_ITEMS_TABLE + " ("
                + COL_NAME + " TEXT PRIMARY KEY, "
                + COL_PORTION_SIZE + " NUMERIC, "
                + COL_MASS_UNIT + " TEXT, "
                + COL_CALORIES + " NUMERIC, "
                + COL_PROTEIN + " NUMERIC, "
                + COL_FAT + " NUMERIC, "
                + COL_CARBS + " NUMERIC, "
                + COL_FIBER + " NUMERIC, "
                + COL_ALCOHOL + " NUMERIC)"
        );
    }

    // Create method to add new entries and remove duplicates
    public void addEntry (@NonNull Food food) {
        getWritableDatabase().delete(FOOD_ITEMS_TABLE, COL_NAME + "='" + food.getName() + "'", null);
        getWritableDatabase().execSQL("INSERT INTO " + FOOD_ITEMS_TABLE
                + "(" + COL_NAME + ", "
                + COL_PORTION_SIZE + ", "
                + COL_MASS_UNIT + ", "
                + COL_CALORIES + ", "
                + COL_PROTEIN + ", "
                + COL_FAT + ", "
                + COL_CARBS + ", "
                + COL_FIBER + ", "
                + COL_ALCOHOL + ")"
                + " VALUES ('"
                + food.getName() + "', "
                + food.getPortionSize() + ", '"
                + food.getMassUnit() + "', "
                + food.getCalories() + ", "
                + food.getProtein() + ", "
                + food.getFat() + ", "
                + food.getCarbs() + ", "
                + food.getFiber() + ", "
                + food.getAlcohol() + ")"
        );
        Controller.foodList.add(food.getName());
    }

    // Create method to fetch a food entry from the database
    public Food getEntry (String queriedFood) {

        Food food;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT *  FROM " + FOOD_ITEMS_TABLE + " WHERE " + COL_NAME + "='" + queriedFood + "'", null);

        if (cursor.moveToFirst()) {
            food = new Food(cursor.getString(0),
                    cursor.getFloat(1),
                    cursor.getString(2),
                    cursor.getFloat(3),
                    cursor.getFloat(4),
                    cursor.getFloat(5),
                    cursor.getFloat(6),
                    cursor.getFloat(7),
                    cursor.getFloat(8));
        } else {
            food = null;
        }
        cursor.close();

        return food;
    }

    // Create method to get all foods from the database as a list
    public List<Food> getFoodList () {
        List <Food> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + FOOD_ITEMS_TABLE + " ORDER BY " + COL_NAME + " ASC";
        Cursor cursor = getReadableDatabase().rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                returnList.add(new Food(cursor.getString(0),
                        cursor.getFloat(1),
                        cursor.getString(2),
                        cursor.getFloat(3),
                        cursor.getFloat(4),
                        cursor.getFloat(5),
                        cursor.getFloat(6),
                        cursor.getFloat(7),
                        cursor.getFloat(8)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }
}