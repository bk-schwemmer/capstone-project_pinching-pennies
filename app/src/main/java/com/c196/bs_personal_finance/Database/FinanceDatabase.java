package com.c196.bs_personal_finance.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.c196.bs_personal_finance.DAO.AccountDAO;
import com.c196.bs_personal_finance.DAO.CategoryDAO;
import com.c196.bs_personal_finance.DAO.TransactionDAO;
import com.c196.bs_personal_finance.DAO.UserDAO;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.Entity.Category;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.Entity.User;

@Database(entities = {User.class, Account.class, Transaction.class, Category.class}, version = 3, exportSchema = false)
public abstract class FinanceDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "financeDatabase.db";

    private static FinanceDatabase financeDatabase;

    static FinanceDatabase getInstance(Context context) {
        if (financeDatabase == null) {
            financeDatabase = Room.databaseBuilder(context, FinanceDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return financeDatabase;
    }

    public abstract UserDAO userDAO();
    public abstract AccountDAO accountDAO();
    public abstract TransactionDAO transactionDAO();
    public abstract CategoryDAO categoryDAO();

}
