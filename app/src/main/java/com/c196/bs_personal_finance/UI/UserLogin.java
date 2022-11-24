package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Category;
import com.c196.bs_personal_finance.Entity.User;
import com.c196.bs_personal_finance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UserLogin extends AppCompatActivity {

    public static final String CURRENT_USER_ID = "currentUserID";
    public static final String PURPOSE = "purpose";

    private Repository repo;
    private String submittedUsername;
    private String submittedPassword;
    private User foundUser;
    private boolean loginSuccessful;

    private Button loginButton;
    private FloatingActionButton addUserButton;
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

                UserLogin.this.runOnUiThread(() -> {
                    if (loginSuccessful) {
                        Toast.makeText(
                                UserLogin.this,
                                "LOGIN SUCCESSFUL",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserLogin.this, Accounts.class);
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

    private final View.OnClickListener addUser = view -> {
        Intent intent = new Intent(UserLogin.this, UserDetails.class);
        intent.putExtra(PURPOSE, getString(R.string.add));
        startActivity(intent);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_login);

        repo = new Repository(getApplication());
        fetchUiElements();
        addCategories();

        // Set Login Button listener
        loginButton.setOnClickListener(login);
        addUserButton.setOnClickListener(addUser);
    }

    private void fetchUiElements() {

        // Buttons
        loginButton = findViewById(R.id.loginButton);
        addUserButton = findViewById(R.id.addUserButton);

        // Text
        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
        loginErrorView = findViewById(R.id.loginError);
    }

    private void addCategories() {
        Thread fetchCategories = new Thread(() -> {
            if (repo.getAllCategories().size() == 0) {
                List<Category> categories = new ArrayList<>();
                categories.add(new Category("Debt", 0));
                categories.add(new Category("Education", 0));
                categories.add(new Category("Entertainment", 0));
                categories.add(new Category("Fees", 0));
                categories.add(new Category("Food", 0));
                categories.add(new Category("Gift", 0));
                categories.add(new Category("Health", 0));
                categories.add(new Category("Home", 0));
                categories.add(new Category("Income", 0));
                categories.add(new Category("Investments", 0));
                categories.add(new Category("Kids", 0));
                categories.add(new Category("Misc", 0));
                categories.add(new Category("Pets", 0));
                categories.add(new Category("Taxes", 0));
                categories.add(new Category("Transfer", 0));
                categories.add(new Category("Transport", 0));
                categories.add(new Category("Utilities", 0));

                for (Category category : categories) {
                    repo.insert(category);
                }
            }
        });
        fetchCategories.start();
    }
}