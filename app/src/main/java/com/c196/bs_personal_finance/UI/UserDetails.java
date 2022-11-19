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
    private String purpose;
    private User newUser;
    private User selectedUser;
    private long selectedUserID;
    String name;
    String username;
    String password;

    private EditText nameEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button cancelButton;
    private Button saveButton;
    private Button deleteButton;
    private ProgressBar deletingProgress;

//    private Spinner userDropdown;

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
//
//        Thread deleteUserThread = new Thread(() -> {
//
//            List<Account> userAccounts = repo.getAccountsByUser(selectedUserID);
//
//            List<Transaction> transactionsInAccount = repo.getTransactionByAccount(currentAccount.getAccountID());
//            if (transactionsInAccount.size() == 0) {
//                deletedUser(currentAccount);
//            }
//        });
//        deleteUserThread.start();
    };

    // TODO DELETE
//    private final AdapterView.OnItemSelectedListener userChosen = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//            populateDetails();
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_user_details);

        repo = new Repository(getApplication());
        fetchUiElements();
        assignPurpose();
        populateDetails();

        // TODO DELETE
//        populateUserDropdown();
//        userDropdown.setOnItemSelectedListener(userChosen);

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

        // TODO DELETE
        // Dropdown
//        userDropdown = findViewById(R.id.userDropdown);

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

//        selectedUser = null;
//        if (userDropdown.getSelectedItem() instanceof User) {
//            selectedUser = (User) userDropdown.getSelectedItem();
//        }
//
//        if (selectedUser != null) {
//            String name = selectedUser.getName();
//            String username = selectedUser.getUsername();
//            String password = selectedUser.getPassword();
//
//
//            if (selectedUser.getName().equals(getString(R.string.new_user))) {
//                nameEdit.setText("");
//            } else {
//                nameEdit.setText(name);
//            }
//            usernameEdit.setText(username);
//            passwordEdit.setText(password);
//        }
    }

    // TODO DELETE
//    private void populateUserDropdown() {
//
//        Thread typeDropdownThread = new Thread(() -> {
//            List<User> users = repo.getAllUsers();
//
//            // Add new user to top of list
//            User newUser = new User(getString(R.string.new_user), "", "");
//            users.add(0, newUser);
//
//            ArrayAdapter<User> adapter = new ArrayAdapter<>(
//                    UserDetails.this,
//                    R.layout.dropdown_item,
//                    users);
//
//            UserDetails.this.runOnUiThread(() -> {
//                userDropdown.setAdapter(adapter);
//
//                // Default to 'New User'
//                userDropdown.setSelection(adapter.getPosition(newUser));
//            });
//        });
//        typeDropdownThread.start();
//    }

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
//                    Log.e(Log.ERROR,"");
                    return false;
                }
                if (repo.delete(account) < 0) return false;
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
                if (repo.delete(transaction) < 0) return false;
            }

            // Verify all assessments have been deleted
            transactions = repo.getTransactionByAccount(accountID);
            return transactions.size() == 0;
        }
    }
}