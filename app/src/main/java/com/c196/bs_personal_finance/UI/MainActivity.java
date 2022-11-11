package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.Entity.User;
import com.c196.bs_personal_finance.R;

public class MainActivity extends AppCompatActivity {

    private Repository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repo = new Repository(getApplication());

        // Test Users
        User b = new User("bkschwemmer", "bks");
        User p = new User("mjreoch", "mjr");

        // Test Accounts
        Account bCash = new Account(1, "bCash", Account.AccountType.CASH, 0.00);
        Account bCheck = new Account(1, "bChecking", Account.AccountType.CHECKING, 500.00);
        Account bLoan = new Account(1, "bCar", Account.AccountType.DEBT, 12000.00);

        Account pSave = new Account(2, "pSavings", Account.AccountType.SAVINGS, 7000.00);
        Account pCred = new Account(2, "pCredit", Account.AccountType.CREDIT, 3000.00);

        // INSERT TEST DATA
//        repo.insert(b);
//        repo.insert(p);
//
//        repo.insert(bCash);
//        repo.insert(bCheck);
//        repo.insert(bLoan);
//
//        repo.insert(pSave);
//        repo.insert(pCred);



    }
}