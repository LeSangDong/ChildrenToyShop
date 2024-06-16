package com.example.toysshop.model;

public class ColorItem {
    private String colorName;
    private int colorValue;

    public ColorItem(String colorName, int colorValue) {
        this.colorName = colorName;
        this.colorValue = colorValue;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public int getColorValue() {
        return colorValue;
    }

    public void setColorValue(int colorValue) {
        this.colorValue = colorValue;
    }
}
