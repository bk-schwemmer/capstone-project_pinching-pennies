package com.c196.bs_personal_finance.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.c196.bs_personal_finance.Entity.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);

    @Update
    int update(User user);

    @Delete
    int delete(User user);

    @Query("SELECT * FROM users ORDER BY username ASC")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE userID = :userID")
    User getUserByID(long userID);

}
