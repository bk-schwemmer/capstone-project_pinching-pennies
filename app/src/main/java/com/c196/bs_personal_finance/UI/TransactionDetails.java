package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Category;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionDetails extends AppCompatActivity {

    private Repository repo;
    private long transactionID;
    private long currentAccountID;
    private Transaction currentTransaction;

    private EditText payeeEdit;
    private EditText amountEdit;
    private TextView dateView;
    private EditText notesEdit;
    private Spinner categoryDropdown;
    private Spinner statusDropdown;
    private Button cancelButton;
    private Button saveButton;
    private Button deleteButton;

    // CLICK LISTENERS
    private final View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(TransactionDetails.this, Transactions.class);
            intent.putExtra(Accounts.CURRENT_ACCOUNT_ID, currentAccountID);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_transaction_details);
        setTitle(getString(R.string.transaction_details));

        repo = new Repository(getApplication());
        fetchUiElements();
        populateDetails();

        cancelButton.setOnClickListener(cancel);

    }

    private void populateDetails() {
        transactionID = getIntent().getLongExtra(Transactions.SELECTED_TRANSACTION_ID, 0);
        currentTransaction = repo.getTransactionByID(transactionID);
        currentAccountID = currentTransaction.getAccountID();
        payeeEdit.setText(currentTransaction.getPayee());
        amountEdit.setText(currentTransaction.getAmountString());
        dateView.setText(currentTransaction.getDate());
        notesEdit.setText(currentTransaction.getNotes());
        populateCategoryDropdown();
        populateStatusDropdown();
    }

    private void populateCategoryDropdown() {
        // TODO put on worker thread
        List<Category> categories = repo.getAllCategories();
        List<String> categoryStrings = new ArrayList<>();
        long categoryID = currentTransaction.getCategory();
        String categoryName = null;

        for (Category category: categories) {
            categoryStrings.add(category.getCategoryName());
            if (category.getCategoryID() == categoryID) {
                categoryName = category.getCategoryName();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                TransactionDetails.this,
                R.layout.dropdown_item,
                categoryStrings);
        categoryDropdown.setAdapter(adapter);

        // Set to selected category
        if (categoryName != null && categoryID > -1) {
            categoryDropdown.setSelection(adapter.getPosition(categoryName));
        }
    }

    private void populateStatusDropdown() {
        // TODO put on worker thread
        Transaction.Status[] statuses = Transaction.Status.values();
        List<String> statusStrings = new ArrayList<>();
        Transaction.Status selectedStatus = currentTransaction.getStatus();
        String statusString = null;

        for (Transaction.Status status: statuses) {
            statusStrings.add(status.toString());
            if (status.equals(selectedStatus)) {
                statusString = status.toString();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                TransactionDetails.this,
                R.layout.dropdown_item,
                statusStrings);
        statusDropdown.setAdapter(adapter);

        // Set to selected category
        if (statusString != null && selectedStatus != null) {
            statusDropdown.setSelection(adapter.getPosition(statusString));
        }
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
    }
}