package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.Entity.User;
import com.c196.bs_personal_finance.R;

import java.util.List;

public class Login extends AppCompatActivity {

    private Repository repo;

    private List<User> allUsers;
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
                        Toast.makeText(Login.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.login_screen);

        repo = new Repository(getApplication());
        fetchUiElements();

        // Set Login Button listener
        loginButton.setOnClickListener(login);

//        Thread thread = new Thread(() -> {
//            allUsers = repo.getAllUsers();
//        });

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