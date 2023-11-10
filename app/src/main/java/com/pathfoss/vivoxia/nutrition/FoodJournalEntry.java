package com.pathfoss.vivoxia.nutrition;

public class FoodJournalEntry {

    private final int rowID;
    private final String name;
    private final float amount;
    private final String massUnit;
    private final float calories;
    private final float protein;
    private final float fat;
    private final float carbs;
    private final float fiber;
    private final float alcohol;

    public FoodJournalEntry(int rowID, String name, float amount, String massUnit, float calories, float protein, float fat, float carbs, float fiber, float alcohol) {
        this.rowID = rowID;
        this.name = name;
        this.amount = amount;
        this.massUnit = massUnit;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.fiber = fiber;
        this.alcohol = alcohol;
    }

    public int getRowID() {
        return rowID;
    }

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }

    public String getMassUnit() {
        return massUnit;
    }

    public float getCalories() {
        return calories;
    }

    public float getProtein() {
        return protein;
    }

    public float getFat() {
        return fat;
    }

    public float getCarbs() {
        return carbs;
    }

    public float getFiber() {
        return fiber;
    }

    public float getAlcohol() {
        return alcohol;
    }
}