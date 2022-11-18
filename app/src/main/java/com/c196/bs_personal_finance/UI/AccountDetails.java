package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.Entity.Category;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AccountDetails extends AppCompatActivity implements AccountDeleteFragment.OnAccountDeletedListener {


    private Repository repo;
    private long accountID;
    private long currentUserID;
    private Account currentAccount;
    private Account newAccount;
    private String accountName;
    private String accountBalance;
    private Account.AccountType accountType;

    private EditText nameEdit;
    private EditText balanceEdit;
    private Spinner typeDropdown;
    private Button cancelButton;
    private Button saveButton;
    private Button deleteButton;
    private ProgressBar deletingProgress;
    private String purpose;

    // CLICK LISTENERS
    private final View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(AccountDetails.this, Accounts.class);
            intent.putExtra(Login.CURRENT_USER_ID, currentUserID);
            startActivity(intent);
        }
    };

    private final View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String newName = nameEdit.getText().toString();

            // Validate Double
            String newAmountString = balanceEdit.getText().toString();
            newAmountString = newAmountString.replace(",", "");
            double newAmount;
            if (!TransactionDetails.validDouble(newAmountString)) {
                AlertDialog.Builder invalidAmount = new AlertDialog.Builder(AccountDetails.this)
                        .setTitle("Invalid Amount")
                        .setMessage("An invalid value was entered as the amount.")
                        .setNeutralButton(R.string.ok, null);
                AlertDialog alertDialog = invalidAmount.create();
                alertDialog.show();
            } else {
                newAmount = Double.parseDouble(newAmountString);

                double finalNewAmount = newAmount;

                // TODO DELETE
                Toast.makeText(AccountDetails.this, "newAmount = " + newAmount, Toast.LENGTH_SHORT).show();

                Thread thread = new Thread(() -> {
                    // Create new transaction from all current fields
                    if (!newName.equals("") && finalNewAmount != 0.0 && typeDropdown.getSelectedItem() != null) {
                        Account.AccountType newType = Account.stringToType(typeDropdown.getSelectedItem().toString());

                        if (purpose.equals("ADD")) {
                            newAccount = new Account(currentUserID, newName, newType, finalNewAmount);

                            // Try to insert
                            AccountDetails.this.runOnUiThread(() -> {
                                if (repo.insert(newAccount) > 0) {
                                    Toast.makeText(
                                            AccountDetails.this,
                                            "Account Added",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AccountDetails.this, Accounts.class);
                                    intent.putExtra(Login.CURRENT_USER_ID, currentUserID);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(
                                            AccountDetails.this,
                                            "Unable to add account",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            newAccount = new Account(currentAccount.getAccountID(), currentUserID,
                                    newName, newType, finalNewAmount);

                            AccountDetails.this.runOnUiThread(() -> {
                                // Try to Update
                                int updatedAccounts = repo.update(newAccount);
                                if (updatedAccounts == 1) {
                                    Toast.makeText(
                                            AccountDetails.this,
                                            "Account Updated",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(AccountDetails.this, Accounts.class);
                                    intent.putExtra(Login.CURRENT_USER_ID, currentUserID);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(
                                            AccountDetails.this,
                                            "Unable to update account",
                                            Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    } else {
                        FragmentManager manager = getSupportFragmentManager();
                        IncompleteEntryFragment warning = new IncompleteEntryFragment();
                        warning.show(manager, "incompleteAccountDialog");
                    }
                });
                thread.start();
            }
        }
    };

//    private final View.OnClickListener deleteAccount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Thread deleteAccountThread = new Thread(() -> {
//                deleteAccount(currentAccount);
//            });
//            deleteAccountThread.start();
//        }
//    };

    private final View.OnClickListener deleteAccountAndTransactions = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Thread deleteTermThread = new Thread(() -> {

                List<Transaction> transactionsInAccount = repo.getTransactionByAccount(currentAccount.getAccountID());
                if (transactionsInAccount.size() == 0) {
                    deleteAccount(currentAccount);
                } else {
                    AccountDetails.this.runOnUiThread(() -> {
                        FragmentManager manager = getSupportFragmentManager();
                        AccountDeleteFragment dialog = new AccountDeleteFragment();
                        dialog.show(manager, "warningDialog");
                        System.out.println("Unable to delete Account. There are transactions assigned to this account.");
                        Toast.makeText(
                                AccountDetails.this,
                                        "This term has courses",
                                        Toast.LENGTH_SHORT).show();
                    });
                }
            });
            deleteTermThread.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_account_details);

        repo = new Repository(getApplication());
        fetchUiElements();
        assignPurpose();
        populateDetails();

        cancelButton.setOnClickListener(cancel);
        saveButton.setOnClickListener(save);
    }

    private void assignPurpose() {
        purpose = getIntent().getStringExtra(Accounts.ACCOUNT_PURPOSE);
        currentUserID = getIntent().getLongExtra(Login.CURRENT_USER_ID, 0);

        if (purpose.equals("ADD")) {

            // TODO Do these need to be assigned for ADD transactions?
//            transactionID = getIntent().getLongExtra(Transactions.SELECTED_TRANSACTION_ID, 0);
//            currentTransaction = repo.getTransactionByID(transactionID);
//            currentAccountID = currentTransaction.getAccountID();
            setTitle(getString(R.string.add_account));
            accountName = "";
            accountBalance = "";
            accountType = Account.AccountType.Cash;

        } else if (purpose.equals("MODIFY")) {
            accountID = getIntent().getLongExtra(Accounts.CURRENT_ACCOUNT_ID, 0);
            currentAccount = repo.getAccountByID(accountID);

            setTitle(getString(R.string.acount_details));
            accountName = currentAccount.getAccountName();
            accountBalance = currentAccount.getCurrentBalanceString();
            accountType = currentAccount.getType();
            loadDeleteButton();
        }
    }

    private void populateDetails() {

        nameEdit.setText(accountName);
        balanceEdit.setText(accountBalance);
        populateTypeDropdown();
    }

    private void populateTypeDropdown() {

        Thread typeDropdownThread = new Thread(() -> {
            Account.AccountType[] types = Account.AccountType.values();
            List<String> typeStrings = new ArrayList<>();
            Account.AccountType selectedType = accountType;
            String typeString = null;

            for (Account.AccountType type : types) {
                typeStrings.add(type.toString());
                if (type.equals(selectedType)) {
                    typeString = type.toString();
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    AccountDetails.this,
                    R.layout.dropdown_item,
                    typeStrings);

            String finalTypeString = typeString;
            AccountDetails.this.runOnUiThread(() -> {
                typeDropdown.setAdapter(adapter);

                // Set to selected category
                if (finalTypeString != null) {
                    typeDropdown.setSelection(adapter.getPosition(finalTypeString));
                }
            });
        });
        typeDropdownThread.start();
    }

    private void fetchUiElements() {

        // Text Views
        nameEdit = findViewById(R.id.nameEdit);
        balanceEdit = findViewById(R.id.balanceEdit);

        // Dropdown Lists
        typeDropdown = findViewById(R.id.accountTypeDropdown);

        // Buttons
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Progress Bars
        deletingProgress = findViewById(R.id.deletingProgressBar);
    }

    private void loadDeleteButton() {
        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(deleteAccountAndTransactions);
    }

    public void deleteAccount(Account account) {
        AccountDetails.this.runOnUiThread(() -> deletingProgress.setVisibility(View.VISIBLE));

        if (repo.delete(account) < 1) {
            AccountDetails.this.runOnUiThread(() -> {
                Toast.makeText(this, "Account Deletion Failed", Toast.LENGTH_SHORT).show();
                deletingProgress.setVisibility(View.GONE);
            });
        } else {
            AccountDetails.this.runOnUiThread(() -> {
                Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AccountDetails.this, Accounts.class);
                intent.putExtra(Login.CURRENT_USER_ID, currentUserID);
                startActivity(intent);
                deletingProgress.setVisibility(View.GONE);
            });
        }
    }

    private boolean deleteTransactionsInAccount(Account account) {
        List<Transaction> transactions = repo.getTransactionByAccount(account.getAccountID());
        boolean deleteSuccessful = true;

        if (transactions.size() != 0) {
            for (Transaction transaction : transactions) {
                if (repo.delete(transaction) < 0) deleteSuccessful = false;
            }

            // Verify all assessments have been deleted
            transactions = repo.getTransactionByAccount(account.getAccountID());
            if (transactions.size() != 0) deleteSuccessful = false;
        }

        return deleteSuccessful;
    }

    @Override
    public void onDeleteOverride() {
        deletingProgress.setVisibility(View.VISIBLE);
        Thread thread = new Thread(() -> {
            if (deleteTransactionsInAccount(currentAccount)) {
                deleteAccount(currentAccount);
            }
        });
        thread.start();
    }
}