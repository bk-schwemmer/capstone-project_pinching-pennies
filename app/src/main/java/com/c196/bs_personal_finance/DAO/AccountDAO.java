package com.c196.bs_personal_finance.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.c196.bs_personal_finance.Entity.Account;

import java.util.List;

@Dao
public interface AccountDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Account account);

    @Update
    int update(Account account);

    @Delete
    int delete(Account account);

    @Query("SELECT * FROM accounts WHERE userID = :userID ORDER BY accountName ASC")
    List<Account> getAccountsByUser(long userID);

    @Query("SELECT * FROM accounts WHERE accountID = :acctID")
    Account getAccountByID(long acctID);

}
