package com.pathfoss.vivoxia.nutrition;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FoodImport {

    private String Name;
    private FoodImportNutrients Nutrients;

    @NonNull
    @Override
    public String toString() {
        return "FoodImport{" +
                "Name='" + Name + '\'' +
                ", Nutrients=" + Nutrients +
                '}';
    }

    @JsonProperty("Name")
    public String getName() {
        return Name;
    }

    @JsonProperty("Nutrients")
    public FoodImportNutrients getNutrients() {
        return Nutrients;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        Name = name;
    }

    @JsonProperty("Nutrients")
    public void setNutrients(FoodImportNutrients nutrients) {
        Nutrients = nutrients;
    }
}
