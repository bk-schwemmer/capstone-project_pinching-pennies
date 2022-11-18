package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.Entity.Category;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.Entity.User;
import com.c196.bs_personal_finance.R;

public class Login extends AppCompatActivity {

    public static final String CURRENT_USER_ID = "currentUserID";

    private Repository repo;

    private String submittedUsername;
    private String submittedPassword;
    private User foundUser;
    private boolean loginSuccessful;

    private Button loginButton;
    private EditText usernameView;
    private EditText passwordView;
    private TextView loginErrorView;

    // CLICK LISTENERS
    // Login
    private final View.OnClickListener login = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            loginErrorView.setVisibility(View.GONE);

            // Get username from UI
            submittedUsername = usernameView.getText().toString();

            loginSuccessful = false;
            Thread getUserThread = new Thread(() -> {
                // Check for username match
                if ((foundUser = repo.getUserByName(submittedUsername)) != null) {
                    // compare given pw to saved pw
                    submittedPassword = passwordView.getText().toString();
                    loginSuccessful = foundUser.checkPassword(submittedPassword);
                }

                Login.this.runOnUiThread(() -> {
                    if (loginSuccessful) {
                        Toast.makeText(
                                Login.this,
                                "LOGIN SUCCESSFUL",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, Accounts.class);
                        intent.putExtra(CURRENT_USER_ID, foundUser.getUserID());
                        startActivity(intent);
                    } else {
                        loginErrorView.setVisibility(View.VISIBLE);
                    }
                });
            });
            getUserThread.start();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_login);

        repo = new Repository(getApplication());
        fetchUiElements();
        createTestData();

        // Set Login Button listener
        loginButton.setOnClickListener(login);
    }

    private void fetchUiElements() {
        loginButton = findViewById(R.id.loginButton);

        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
        loginErrorView = findViewById(R.id.loginError);
    }

    private void createTestData() {
        // Test Users
        User b = new User("bkschwemmer", "bks");
        User p = new User("mjreoch", "mjr");

        // Test Accounts
        Account bCash = new Account(1, "bCash", Account.AccountType.Cash, 0.00);
        Account bCheck = new Account(1, "bChecking", Account.AccountType.Checking, 500.00);
        Account bLoan = new Account(1, "bCar", Account.AccountType.Debt, -12000.00);

        Account pSave = new Account(2, "pSavings", Account.AccountType.Savings, 7000.00);
        Account pCred = new Account(2, "pCredit", Account.AccountType.Credit, -3000.00);

        Transaction b1 = new Transaction(1, 20.99, "Fred Meyer", "05/15/22", Transaction.Status.Reconciled, "", 1);
        Transaction b2 = new Transaction(2, 45.56, "Costco Gas", "07/09/22", Transaction.Status.Reconciled, "", 2);
        Transaction b3 = new Transaction(2, 328.00, "Idaho State", "11/01/22", Transaction.Status.Reconciled, "", 4);
        Transaction b4 = new Transaction(2, 260.00, "Child Support", "11/15/22", Transaction.Status.Reconciled, "Oliver 11.15 & 11.29", 3);
        Transaction b5 = new Transaction(2, 32.44, "Maverik", "11/15/22", Transaction.Status.Reconciled, "", 2);

        Category groceries = new Category("Groceries", 0);
        Category transportation = new Category("Transport", 0);
        Category kids = new Category("Kids", 0);
        Category childSupport = new Category("Child Support", 3);

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
//
//        repo.insert(groceries);
//        repo.insert(transportation);
//        repo.insert(kids);
//        repo.insert(childSupport);
//
//        repo.insert(b1);
//        repo.insert(b2);
//        repo.insert(b3);
//        repo.insert(b4);
//        repo.insert(b5);

    }
}