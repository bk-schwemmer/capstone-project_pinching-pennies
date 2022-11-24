package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.Entity.User;
import com.c196.bs_personal_finance.R;

import java.util.List;

public class UserDetails extends AppCompatActivity implements UserDeletionFragment.OnUserDeletedListener {

    private Repository repo;
    private User newUser;
    private User selectedUser;
    private long selectedUserID;
    private String purpose;
    private String name;
    private String username;
    private String password;

    private EditText nameEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button cancelButton;
    private Button saveButton;
    private Button deleteButton;
    private ProgressBar deletingProgress;

    // CLICK LISTENERS
    private final View.OnClickListener cancel = view -> {
        Intent intent = new Intent(UserDetails.this, UserLogin.class);
        startActivity(intent);
    };

    private final View.OnClickListener save = view -> {

            String newName = nameEdit.getText().toString();
            String newUsername = usernameEdit.getText().toString();
            String newPassword = passwordEdit.getText().toString();

            Thread thread = new Thread(() -> {
                // Create new transaction from all current fields
                if (!newName.equals("") && !newUsername.equals("") && !newPassword.equals("")) {
                    if (purpose.equals("ADD")) {
                        newUser = new User(newName, newUsername, newPassword);

                        // Try to insert
                        UserDetails.this.runOnUiThread(() -> {
                            if (repo.insert(newUser) > 0) {
                                Toast.makeText(
                                        UserDetails.this,
                                        "User added",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserDetails.this, UserLogin.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(
                                        UserDetails.this,
                                        "Unable to add user",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        newUser = new User(selectedUser.getUserID(), newName, newUsername, newPassword);

                        // Try to update
                        UserDetails.this.runOnUiThread(() -> {
                            if (repo.update(newUser) > 0) {
                                Toast.makeText(
                                        UserDetails.this,
                                        "User updated",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserDetails.this, UserLogin.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(
                                        UserDetails.this,
                                        "Unable to update user",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    FragmentManager manager = getSupportFragmentManager();
                    IncompleteEntryFragment warning = new IncompleteEntryFragment();
                    warning.show(manager, "incompleteUserDialog");
                }
            });
            thread.start();
    };

    private final View.OnClickListener deleteUser =  view -> {

        FragmentManager manager = getSupportFragmentManager();
        UserDeletionFragment dialog = new UserDeletionFragment();
        dialog.show(manager, "warningDialog");
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_user_details);

        repo = new Repository(getApplication());
        fetchUiElements();
        assignPurpose();
        populateDetails();

        cancelButton.setOnClickListener(cancel);
        saveButton.setOnClickListener(save);
    }

    private void fetchUiElements() {

        // Buttons
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Text
        nameEdit = findViewById(R.id.nameEdit);
        usernameEdit = findViewById(R.id.usernameEdit);
        passwordEdit = findViewById(R.id.passwordEdit);

        // Progress Bars
        deletingProgress = findViewById(R.id.deletingProgressBar);
    }

    private void assignPurpose() {
        purpose = getIntent().getStringExtra(UserLogin.PURPOSE);

        if (purpose.equals("ADD")) {
            setTitle(getString(R.string.add_user));
            name = "";
            username = "";
            password = "";
        } else if (purpose.equals("MODIFY")) {
            selectedUserID = getIntent().getLongExtra(UserLogin.CURRENT_USER_ID, 0);
            selectedUser = repo.getUserByID(selectedUserID);
            setTitle(getString(R.string.user_details));
            name = selectedUser.getName();
            username = selectedUser.getUsername();
            password = selectedUser.getPassword();
            loadDeleteButton();
        }
    }

    private void populateDetails() {
        nameEdit.setText(name);
        usernameEdit.setText(username);
        passwordEdit.setText(password);
    }

    private void loadDeleteButton() {
        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(deleteUser);
    }

    @Override
    public void onDeleteOverride() {
        deletingProgress.setVisibility(View.VISIBLE);

        Thread thread = new Thread(() -> {
            if (deletedUser(selectedUser)) {
                UserDetails.this.runOnUiThread(() -> {
                    Toast.makeText(this, "User Deletion Failed", Toast.LENGTH_LONG).show();
                    deletingProgress.setVisibility(View.GONE);
                });
            } else {
                UserDetails.this.runOnUiThread(() -> {
                    Toast.makeText(this, "User Deleted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UserDetails.this, UserLogin.class);
                    startActivity(intent);
                    deletingProgress.setVisibility(View.GONE);
                });
            }
        });
        thread.start();
    }

    private boolean deletedUser(User user) {
        if (deletedAccountsForUser(selectedUserID)) {
            return (repo.delete(user) < 1);
        } else {
            return false;
        }
    }

    private boolean deletedAccountsForUser(long userID) {
        List<Account> accounts = repo.getAccountsByUser(userID);

        if (accounts.size() == 0) return true;
        else {
            for (Account account : accounts) {
                if (!deletedTransactionsForAccount(account.getAccountID())) {
                    Log.e("UserDetails","Failed to delete all transactions in account " + account.getAccountName());
                    return false;
                }
                if (repo.delete(account) < 0){
                    Log.e("UserDetails","Failed to delete account " + account.getAccountName());
                    return false;
                }
            }

            // Verify all accounts have been deleted
            accounts = repo.getAccountsByUser(userID);
            return accounts.size() == 0;
        }
    }

    private boolean deletedTransactionsForAccount(long accountID) {
        List<Transaction> transactions = repo.getTransactionByAccount(accountID);

        if (transactions.size() == 0) return true;
        else {
            for (Transaction transaction : transactions) {
                if (repo.delete(transaction) < 0) {
                    Log.e("UserDetails","Failed to delete transaction: ID " + transaction.getTransactionID());
                    return false;
                }
            }

            // Verify all assessments have been deleted
            transactions = repo.getTransactionByAccount(accountID);
            return transactions.size() == 0;
        }
    }
}