package com.c196.bs_personal_finance.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private long categoryID;

    private String categoryName;
    private long parentID;

    public Category(String categoryName, long parentID) {
        this.categoryName = categoryName;
        this.parentID = parentID;
    }

    @Ignore
    public Category(long categoryID, String categoryName, long parentID) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.parentID = parentID;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getParentID() {
        return parentID;
    }

    public void setParentID(long parentID) {
        this.parentID = parentID;
    }

    @NonNull
    @Override
    public String toString() {
        return "Category{" +
                "categoryID=" + categoryID +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
