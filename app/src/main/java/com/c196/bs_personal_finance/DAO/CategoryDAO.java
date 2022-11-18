package com.c196.bs_personal_finance.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.c196.bs_personal_finance.Entity.Category;

import java.util.List;

@Dao
public interface CategoryDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Category category);

    @Update
    int update(Category category);

    @Delete
    int delete(Category category);

    @Query("SELECT * FROM categories ORDER BY categoryName ASC")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories WHERE categoryID = :catID OR parentID = :catID")
    Category getCategoryByID(long catID);

    @Query("SELECT * FROM categories WHERE categoryName = :catName")
    Category getCategoryByName(String catName);
}
