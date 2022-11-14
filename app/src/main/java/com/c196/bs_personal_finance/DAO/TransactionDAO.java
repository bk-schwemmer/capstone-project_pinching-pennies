package com.c196.bs_personal_finance.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.c196.bs_personal_finance.Entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Transaction transaction);

    @Update
    int update(Transaction transaction);

    @Delete
    int delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE accountID IN (SELECT accountID FROM accounts WHERE userID = :userID) ORDER BY date DESC")
    List<Transaction> getTransactionsByUser(long userID);

    @Query("SELECT * FROM transactions WHERE accountID = :acctID ORDER BY date DESC")
    List<Transaction> getTransactionByAccount(long acctID);

    @Query("SELECT * FROM transactions WHERE transactionID = :transID")
    Transaction getTransactionByID(long transID);

    @Query("SELECT * FROM transactions WHERE amount = :amt")
    Transaction getTransactionByAmount(double amt);

    @Query("SELECT * FROM transactions WHERE payee = :payee")
    Transaction getTransactionByPayee(String payee);

    @Query("SELECT * FROM transactions WHERE accountID = :acctID AND category = :catID")
    List<Transaction> getTransactionsByCategory(long acctID, long catID);

}
