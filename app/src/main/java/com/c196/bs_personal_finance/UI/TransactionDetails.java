package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Category;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TransactionDetails extends AppCompatActivity {

    // Members
    private Repository repo;
    private long currentAccountID;
    private Transaction currentTransaction;
    private Transaction newTransaction;
    private String payee;
    private String amount;
    private String date;
    private String notes;
    private Category category;
    private Transaction.Status status;

    // Views
    private EditText payeeEdit;
    private EditText amountEdit;
    private TextView dateView;
    private EditText notesEdit;
    private Spinner categoryDropdown;
    private Spinner statusDropdown;
    private Button cancelButton;
    private Button saveButton;
    private Button deleteButton;
    private ImageButton datePicker;
    private ProgressBar deletingProgress;
    private String transactionPurpose;

    // Dates
    final Calendar calendar = Calendar.getInstance();
    final String dateFormat = "MM/dd/yyyy";
    final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    final String currentDate = sdf.format(new Date());
    private DatePickerDialog.OnDateSetListener setDate;


    // CLICK LISTENERS
    private final View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(TransactionDetails.this, Transactions.class);
            intent.putExtra(Accounts.CURRENT_ACCOUNT_ID, currentAccountID);
            startActivity(intent);
        }
    };

    private final View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String newPayee = payeeEdit.getText().toString();

            // Validate Double
            String newAmountString = amountEdit.getText().toString();
            newAmountString = newAmountString.replace(",", "");
            double newAmount;
            if (!validCurrency(newAmountString)) {
                AlertDialog.Builder invalidAmount = new AlertDialog.Builder(TransactionDetails.this)
                        .setTitle("Invalid Amount")
                        .setMessage("An invalid value was entered as the amount.\n" +
                                "Please ensure you only use digits and one period.")
                        .setNeutralButton(R.string.ok, null);
                AlertDialog alertDialog = invalidAmount.create();
                alertDialog.show();
            } else {
                newAmount = Double.parseDouble(newAmountString);
                double finalNewAmount = newAmount;
                String newDate = dateView.getText().toString();
                String newNotes = notesEdit.getText().toString();

                Thread thread = new Thread(() -> {
                    // Create new transaction from all current fields
                    if (!newPayee.equals("") && !newDate.equals("") && finalNewAmount != 0.0 &&
                            statusDropdown.getSelectedItem() != null && categoryDropdown.getSelectedItem() != null) {
                        Transaction.Status newStatus = Transaction.stringToStatus(statusDropdown.getSelectedItem().toString());
                        Category newCategory = repo.getCategoryByName(categoryDropdown.getSelectedItem().toString());
                        long newCategoryID = newCategory.getCategoryID();

                        if (transactionPurpose.equals("ADD")) {
                            newTransaction = new Transaction(currentAccountID, finalNewAmount,
                                    newPayee, newDate, newStatus, newNotes, newCategoryID);

                            // Try to insert
                            TransactionDetails.this.runOnUiThread(() -> {
                                if (repo.insert(newTransaction) > 0) {
                                    Toast.makeText(
                                            TransactionDetails.this,
                                            "Transaction Added",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(TransactionDetails.this, Transactions.class);
                                    intent.putExtra(Accounts.CURRENT_ACCOUNT_ID, currentAccountID);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(
                                            TransactionDetails.this,
                                            "Unable to add transaction",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            newTransaction = new Transaction(
                                    currentTransaction.getTransactionID(), currentAccountID,
                                    finalNewAmount, newPayee, newDate, newStatus, newNotes, newCategoryID);

                            TransactionDetails.this.runOnUiThread(() -> {
                                // Try to Update
                                int updatedTransactions = repo.update(newTransaction);
                                if (updatedTransactions == 1) {
                                    Toast.makeText(
                                            TransactionDetails.this,
                                            "Transaction Updated",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(TransactionDetails.this, Transactions.class);
                                    intent.putExtra(Accounts.CURRENT_ACCOUNT_ID, currentAccountID);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(
                                            TransactionDetails.this,
                                            "Unable to update transaction",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        FragmentManager manager = getSupportFragmentManager();
                        IncompleteEntryFragment warning = new IncompleteEntryFragment();
                        warning.show(manager, "incompleteTransactionDialog");
                    }
                });
                thread.start();
            }
        }
    };

    private final View.OnClickListener deleteTransaction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Thread deleteTransactionThread = new Thread(() -> deleteTransaction(currentTransaction));
            deleteTransactionThread.start();
        }
    };

    private final View.OnClickListener pickDate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String selectedDate = dateView.getText().toString();
            try {
                calendar.setTime(sdf.parse(selectedDate));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("TransactionDetails", "Parse exception");
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("TransactionDetails", "Date Parse is null");
            }
            new DatePickerDialog(
                    TransactionDetails.this,
                    setDate,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_transaction_details);

        repo = new Repository(getApplication());
        fetchUiElements();
        assignPurpose();
        populateDetails();

        cancelButton.setOnClickListener(cancel);
        saveButton.setOnClickListener(save);
        datePicker.setOnClickListener(pickDate);

    }

    private void fetchUiElements() {

        // Text Views
        payeeEdit = findViewById(R.id.payeeEdit);
        amountEdit = findViewById(R.id.amountEdit);
        dateView = findViewById(R.id.dateView);
        notesEdit = findViewById(R.id.notesEdit);

        // Dropdown Lists
        categoryDropdown = findViewById(R.id.categoryDropdown);
        statusDropdown = findViewById(R.id.statusDropdown);

        // Buttons
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        datePicker = findViewById(R.id.datePickerButton);

        // Progress Bars
        deletingProgress = findViewById(R.id.deletingProgressBar);
    }

    private void assignPurpose() {
        transactionPurpose = getIntent().getStringExtra(Transactions.TRANSACTION_PURPOSE);
        if (transactionPurpose.equals("ADD")) {
            currentAccountID = getIntent().getLongExtra(Accounts.CURRENT_ACCOUNT_ID, 0);
            setTitle(getString(R.string.add_transaction));
            payee = "";
            amount = "";
            date = currentDate;
            category = null;
            status = Transaction.Status.Reconciled;
            notes = "";
        } else if (transactionPurpose.equals("MODIFY")) {
            long transactionID = getIntent().getLongExtra(Transactions.SELECTED_TRANSACTION_ID, 0);
            currentTransaction = repo.getTransactionByID(transactionID);
            currentAccountID = currentTransaction.getAccountID();

            setTitle(getString(R.string.transaction_details));
            payee = currentTransaction.getPayee();
            amount = currentTransaction.getAmountString();
            date = currentTransaction.getDate();
            notes = currentTransaction.getNotes();
            category = repo.getCategoryByID(currentTransaction.getCategory());
            status = currentTransaction.getStatus();
            loadDeleteButton();
        }
    }

    private void populateDetails() {

        payeeEdit.setText(payee);
        amountEdit.setText(amount);
        dateView.setText(date);
        notesEdit.setText(notes);
        populateCategoryDropdown();
        populateStatusDropdown();

        setDate = (datePicker, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateView.setText(sdf.format(calendar.getTime()));
        };

    }

    private void populateCategoryDropdown() {
        Thread categoryDropdownThread = new Thread(() -> {
            List<Category> categories = repo.getAllCategories();
            List<String> categoryStrings = new ArrayList<>();
            long categoryID = -1;
            if (Objects.equals(transactionPurpose, "MODIFY")) {
                categoryID = category.getCategoryID();
            }

            String categoryName = null;
            for (Category category: categories) {
                categoryStrings.add(category.getCategoryName());
                if (category.getCategoryID() == categoryID) {
                    categoryName = category.getCategoryName();
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    TransactionDetails.this, R.layout.dropdown_item, categoryStrings);

            String finalCategoryName = categoryName;
            long finalCategoryID = categoryID;
            TransactionDetails.this.runOnUiThread(() -> {
                categoryDropdown.setAdapter(adapter);

                // Set to selected category
                if (finalCategoryName != null && finalCategoryID > -1) {
                    categoryDropdown.setSelection(adapter.getPosition(finalCategoryName));
                }
            });
        });
        categoryDropdownThread.start();
    }

    private void populateStatusDropdown() {
        Thread statusDropdownThread = new Thread(() -> {
            Transaction.Status[] statuses = Transaction.Status.values();
            List<String> statusStrings = new ArrayList<>();
            Transaction.Status selectedStatus = status;
            String statusString = null;

            for (Transaction.Status status : statuses) {
                statusStrings.add(status.toString());
                if (status.equals(selectedStatus)) {
                    statusString = status.toString();
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    TransactionDetails.this, R.layout.dropdown_item, statusStrings);
            String finalStatusString = statusString;
            TransactionDetails.this.runOnUiThread(() -> {
                statusDropdown.setAdapter(adapter);

                // Set to selected category
                if (finalStatusString != null) {
                    statusDropdown.setSelection(adapter.getPosition(finalStatusString));
                }
            });
        });
        statusDropdownThread.start();
    }

    private void loadDeleteButton() {
        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(deleteTransaction);
    }

    private void deleteTransaction(Transaction transaction) {
        TransactionDetails.this.runOnUiThread(() -> deletingProgress.setVisibility(View.VISIBLE));

        if (repo.delete(transaction) < 1) {
            TransactionDetails.this.runOnUiThread(() -> {
                Toast.makeText(this, "Transaction Deletion Failed", Toast.LENGTH_SHORT).show();
                deletingProgress.setVisibility(View.GONE);
            });
        } else {
            TransactionDetails.this.runOnUiThread(() -> {
                Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TransactionDetails.this, Transactions.class);
                intent.putExtra(Accounts.CURRENT_ACCOUNT_ID, currentAccountID);
                startActivity(intent);
                deletingProgress.setVisibility(View.GONE);
            });
        }
    }

    public static boolean validCurrency(String string) {

        final int DECIMAL_PLACES = 2;
        int countDots = 0;
        int countDigits = 0;

        // Empty String
        if (string.length() == 0) return false;

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                if (string.charAt(i) == '.') {
                    // Decimal is more than 2 places from last digit
                    if (i + DECIMAL_PLACES < string.length() - 1) return false;
                    else countDots++;

                    // More than one decimal
                    if (countDots > 1) return false;

                // Negative sign anywhere other than front
                } else if (string.charAt(i) == '-') {
                    if (i > 0) return false;

                // If the character is anything other than a digit, ".", or "-" it's wrong
                } else {
                    return false;
                }
            } else {
                countDigits++;
            }
        }
        return countDigits > 0;
    }
}