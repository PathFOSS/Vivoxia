package com.pathfoss.vivoxia.nutrition;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FoodImportNutrients {

        private float Protein;
        private float Fat;
        private float Carbs;
        private float Calories;

        @NonNull
        @Override
        public String toString() {
                return "FoodImportNutrients{" +
                        "Protein=" + Protein +
                        ", Fat=" + Fat +
                        ", Carbs=" + Carbs +
                        ", Calories=" + Calories +
                        '}';
        }

        @JsonProperty("Protein")
        public float getProtein() {
                return Protein;
        }

        @JsonProperty("Fat")
        public float getFat() {
                return Fat;
        }

        @JsonProperty("Carbs")
        public float getCarbs() {
                return Carbs;
        }

        @JsonProperty("Calories")
        public float getCalories() {
                return Calories;
        }

        @JsonProperty("Protein")
        public void setProtein(float protein) {
                Protein = protein;
        }

        @JsonProperty("Fat")
        public void setFat(float fat) {
                Fat = fat;
        }

        @JsonProperty("Carbs")
        public void setCarbs(float carbs) {
                Carbs = carbs;
        }

        @JsonProperty("Calories")
        public void setCalories(float calories) {
                Calories = calories;
        }
}
