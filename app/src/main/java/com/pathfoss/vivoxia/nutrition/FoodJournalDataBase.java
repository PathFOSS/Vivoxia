package com.pathfoss.vivoxia.nutrition;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FoodJournalDataBase extends SQLiteOpenHelper {

    // Define food composition table
    private final String FOOD_JOURNAL_TABLE = "FOOD_JOURNAL_TABLE";
    private final String COL_UPDATED_ID = "UPDATED_ID";
    private final String COL_DATE = "DATE";
    private final String COL_FOOD_NAME = "FOOD_NAME";
    private final String COL_AMOUNT = "AMOUNT";
    private final String COL_MASS_UNIT = "MASS_UNIT";
    private final String COL_CALORIES = "CALORIES";
    private final String COL_PROTEIN = "PROTEIN";
    private final String COL_FAT = "FAT";
    private final String COL_CARBS = "CARBS";
    private final String COL_FIBER = "FIBER";
    private final String COL_ALCOHOL = "ALCOHOL";

    public FoodJournalDataBase(@Nullable Context context) {super(context, "food_journal_table.db", null, 1);}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
    
    @Override
    public void onCreate(@NonNull SQLiteDatabase sqLiteDatabase) {

        // Update table if it does not exist
        sqLiteDatabase.execSQL("CREATE TABLE " + FOOD_JOURNAL_TABLE
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_UPDATED_ID + " NUMERIC, "
                + COL_DATE + " TEXT, "
                + COL_FOOD_NAME + " TEXT, "
                + COL_AMOUNT + " NUMERIC, "
                + COL_MASS_UNIT + " TEXT, "
                + COL_CALORIES + " NUMERIC, "
                + COL_PROTEIN + " NUMERIC, "
                + COL_FAT + " NUMERIC, "
                + COL_CARBS + " NUMERIC, "
                + COL_FIBER + " NUMERIC, "
                + COL_ALCOHOL + " NUMERIC)"
        );
    }

    // Create method to add entries based on added set number
    public void addEntry (String date, String name, float amount, String massUnit, float calories, float protein, float fat, float carbs, float fiber, float alcohol) {
        getWritableDatabase().execSQL("INSERT INTO " + FOOD_JOURNAL_TABLE + " ("
                + COL_UPDATED_ID + ", "
                + COL_DATE + ", "
                + COL_FOOD_NAME + ", "
                + COL_AMOUNT + ", "
                + COL_MASS_UNIT + ", "
                + COL_CALORIES + ", "
                + COL_PROTEIN + ", "
                + COL_FAT + ", "
                + COL_CARBS + ", "
                + COL_FIBER + ", "
                + COL_ALCOHOL + ") "
                + "VALUES ("
                + "COALESCE((SELECT MAX(ID) + 1 FROM " + FOOD_JOURNAL_TABLE + "), 1), "
                + "'" + date + "', "
                + "'" + name + "', "
                + amount + ", "
                + "'" + massUnit + "', "
                + calories + ", "
                + protein + ", "
                + fat + ", "
                + carbs + ", "
                + fiber + ", "
                + alcohol + ")"
        );
    }

    // Create method to delete entries
    public void deleteEntry (Integer rowId) {
        getWritableDatabase().execSQL("DELETE FROM " + FOOD_JOURNAL_TABLE + " WHERE " + COL_UPDATED_ID + " = " + rowId);
    }

    // Create method to duplicate entries
    public void duplicateEntry (Integer rowID) {
        getWritableDatabase().execSQL("INSERT INTO " + FOOD_JOURNAL_TABLE + " ("
                + COL_UPDATED_ID + ", "
                + COL_DATE + ", "
                + COL_FOOD_NAME + ", "
                + COL_AMOUNT + ", "
                + COL_MASS_UNIT + ", "
                + COL_CALORIES + ", "
                + COL_PROTEIN + ", "
                + COL_FAT + ", "
                + COL_CARBS + ", "
                + COL_FIBER + ", "
                + COL_ALCOHOL + ") "
                + " SELECT "
                + "(SELECT MAX(ID) + 1 FROM " + FOOD_JOURNAL_TABLE + "), "
                + COL_DATE + ", "
                + COL_FOOD_NAME + ", "
                + COL_AMOUNT + ", "
                + COL_MASS_UNIT + ", "
                + COL_CALORIES + ", "
                + COL_PROTEIN + ", "
                + COL_FAT + ", "
                + COL_CARBS + ", "
                + COL_FIBER + ", "
                + COL_ALCOHOL
                + " FROM " + FOOD_JOURNAL_TABLE
                + " WHERE " + COL_UPDATED_ID + " = " + rowID
                + " LIMIT 1"
        );
    }

    // Create method to get entries for a specific date
    public List<FoodJournalEntry> getEntriesForDate (String date) {

        // Fetch entries from database
        List<FoodJournalEntry> foodJournalEntryList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + FOOD_JOURNAL_TABLE + " WHERE " + COL_DATE + " = '" + date + "' ORDER BY " + COL_UPDATED_ID + ", ID", null);

        if (cursor.moveToFirst()) {
            do {
                foodJournalEntryList.add(new FoodJournalEntry(cursor.getInt(1),
                        cursor.getString(3),
                        cursor.getFloat(4),
                        cursor.getString(5),
                        cursor.getFloat(6),
                        cursor.getFloat(7),
                        cursor.getFloat(8),
                        cursor.getFloat(9),
                        cursor.getFloat(10),
                        cursor.getFloat(11)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return  foodJournalEntryList;
    }

    // Create method to swap entries
    public void swapEntries (int rowID1, int rowID2) {
        getWritableDatabase().execSQL("UPDATE " + FOOD_JOURNAL_TABLE
                + " SET " + COL_UPDATED_ID + " = CASE WHEN " + COL_UPDATED_ID + " = " + rowID1 + " THEN " + rowID2 + " ELSE " + rowID1
                + " END WHERE " + COL_UPDATED_ID + " IN (" + rowID1 + ", " + rowID2 + ")"
        );
    }

    // Create a method to get customized data based on user input
    public LinkedHashMap<String, Float> getFoodJournalEntriesByQuery (String COL_QUERIED) {

        LinkedHashMap<String, Float> calorieMap = new LinkedHashMap<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT " + COL_DATE + ", SUM(" + COL_QUERIED + ") FROM " + FOOD_JOURNAL_TABLE + " GROUP BY " + COL_DATE + " ORDER BY " + COL_DATE + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                calorieMap.put(cursor.getString(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return  calorieMap;
    }

    public float getDailySum (String COL_QUERIED, String date) {

        Cursor cursor = getReadableDatabase().rawQuery("SELECT SUM(" + COL_QUERIED + ") FROM " + FOOD_JOURNAL_TABLE + " WHERE " + COL_DATE + " = '" + date + "'", null);
        float returnValue;

        if (cursor.moveToFirst()) {
            returnValue = cursor.getFloat(0);
        } else {
            returnValue = 0f;
        }

        cursor.close();
        return returnValue;
    }
}