package com.example.skillshub.model;

public class customModel {

    private final int iconID;
    private final String mainCategoryName;

    public customModel(int iconID, String mainCategoryName){
        this.iconID = iconID;
        this.mainCategoryName = mainCategoryName;
    }

    public int getIconID() {
        return iconID;
    }

    public String getMainCategory() {
        return mainCategoryName;
    }
}


