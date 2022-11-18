package com.c196.bs_personal_finance.Database;

import android.app.Application;

import com.c196.bs_personal_finance.DAO.AccountDAO;
import com.c196.bs_personal_finance.DAO.CategoryDAO;
import com.c196.bs_personal_finance.DAO.TransactionDAO;
import com.c196.bs_personal_finance.DAO.UserDAO;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.Entity.Category;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.Entity.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private static final int NUMBER_OF_THREADS = 8;
    private final UserDAO mUserDAO;
    private final AccountDAO mAccountDAO;
    private final TransactionDAO mTransactionDAO;
    private final CategoryDAO mCategoryDAO;

    private User user;
    private Account account;
    private Transaction transaction;
    private Category category;

    private long newUserID;
    private long newAccountID;
    private long newTransactionID;
    private long newCategoryID;
    private int updatedUsers;
    private int updatedAccounts;
    private int updatedTransactions;
    private int updatedCategories;
    private int deletedUsers;
    private int deletedAccounts;
    private int deletedTransactions;
    private int deletedCategories;

    private List<User> mUsers;
    private List<Account> mAccounts;
    private List<Transaction> mTransactions;
    private List<Category> mCategories;

    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        FinanceDatabase db = FinanceDatabase.getInstance(application);
        mUserDAO = db.userDAO();
        mAccountDAO = db.accountDAO();
        mTransactionDAO = db.transactionDAO();
        mCategoryDAO = db.categoryDAO();
    }


    // USERS

    public long insert(User user) {
        databaseExecutor.execute(()-> newUserID = mUserDAO.insert(user));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newUserID;
    }

    public int update(User user) {
        databaseExecutor.execute(()-> updatedUsers = mUserDAO.update(user));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return updatedUsers;
    }

    public int delete(User user) {
        databaseExecutor.execute(()-> deletedUsers = mUserDAO.delete(user));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return deletedUsers;
    }

    public List<User> getAllUsers() {
        databaseExecutor.execute(()-> mUsers = mUserDAO.getAllUsers());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mUsers;
    }

    public User getUserByID(long userID) {
        databaseExecutor.execute(()-> user = mUserDAO.getUserByID(userID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUserByName(String username) {
        databaseExecutor.execute(()-> user = mUserDAO.getUserByName(username));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user;
    }


    // ACCOUNTS
    public long insert(Account account) {
        databaseExecutor.execute(()-> newAccountID = mAccountDAO.insert(account));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newAccountID;
    }

    public int update(Account account) {
        databaseExecutor.execute(()-> updatedAccounts = mAccountDAO.update(account));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return updatedAccounts;
    }

    public int delete(Account account) {
        databaseExecutor.execute(()-> deletedAccounts = mAccountDAO.delete(account));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return deletedAccounts;
    }

    public List<Account> getAccountsByUser(long userID) {
        databaseExecutor.execute(()-> mAccounts = mAccountDAO.getAccountsByUser(userID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mAccounts;
    }

    public Account getAccountByID(long acctID) {
        databaseExecutor.execute(()-> account = mAccountDAO.getAccountByID(acctID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return account;
    }

    public int addTransaction(long acctID, double amount) {
        databaseExecutor.execute(()-> updatedAccounts = mAccountDAO.addTransaction(acctID, amount));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return updatedAccounts;
    }


    // TRANSACTIONS

    public long insert(Transaction transaction) {
        databaseExecutor.execute(()-> newTransactionID = mTransactionDAO.insert(transaction));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newTransactionID;
    }

    public int update(Transaction transaction) {
        databaseExecutor.execute(()-> updatedTransactions = mTransactionDAO.update(transaction));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return updatedTransactions;
    }

    public int delete(Transaction transaction) {
        databaseExecutor.execute(()-> deletedTransactions = mTransactionDAO.delete(transaction));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return deletedTransactions;
    }

    public List<Transaction> getTransactionsByUser(long userID) {
        databaseExecutor.execute(()-> mTransactions = mTransactionDAO.getTransactionsByUser(userID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mTransactions;
    }

    public List<Transaction> getTransactionByAccount(long acctID) {
        databaseExecutor.execute(()-> mTransactions = mTransactionDAO.getTransactionByAccount(acctID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mTransactions;
    }

    public List<Transaction> getTransactionsByCategory(long acctID, long catID) {
        databaseExecutor.execute(()-> mTransactions = mTransactionDAO.getTransactionsByCategory(acctID, catID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mTransactions;
    }

    public Transaction getTransactionByID(long transID) {
        databaseExecutor.execute(()-> transaction = mTransactionDAO.getTransactionByID(transID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    public Transaction getTransactionByAmount(double amt) {
        databaseExecutor.execute(()-> transaction = mTransactionDAO.getTransactionByAmount(amt));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    public Transaction getTransactionByPayee(String payee) {
        databaseExecutor.execute(()-> transaction = mTransactionDAO.getTransactionByPayee(payee));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return transaction;
    }



    // CATEGORIES
    public long insert(Category category) {
        databaseExecutor.execute(()-> newCategoryID = mCategoryDAO.insert(category));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newCategoryID;
    }

    public int update(Category category) {
        databaseExecutor.execute(()-> updatedCategories = mCategoryDAO.update(category));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return updatedCategories;
    }

    public int delete(Category category) {
        databaseExecutor.execute(()-> deletedCategories = mCategoryDAO.delete(category));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return deletedCategories;
    }

    public List<Category> getAllCategories() {
        databaseExecutor.execute(()-> mCategories = mCategoryDAO.getAllCategories());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mCategories;
    }

    public Category getCategoryByID(long catID) {
        databaseExecutor.execute(()-> category = mCategoryDAO.getCategoryByID(catID));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return category;
    }

    public Category getCategoryByName(String catName) {
        databaseExecutor.execute(()-> category = mCategoryDAO.getCategoryByName(catName));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return category;
    }
}
