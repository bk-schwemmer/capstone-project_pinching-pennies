package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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
    private Transaction currentTransaction;

    private EditText payeeEdit;
    private EditText amountEdit;
    private TextView dateView;
    private Spinner categoryDropdown;
    private EditText notesEdit;
    private Spinner statusDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_transaction_details);
        setTitle(getString(R.string.transaction_details));

        repo = new Repository(getApplication());
        fetchUiElements();
        populateDetails();
    }

    private void populateDetails() {
        transactionID = getIntent().getLongExtra(Transactions.SELECTED_TRANSACTION_ID, 0);
        currentTransaction = repo.getTransactionByID(transactionID);
        payeeEdit.setText(currentTransaction.getPayee());
        amountEdit.setText(currentTransaction.getAmountString());
        dateView.setText(currentTransaction.getDate());
        notesEdit.setText(currentTransaction.getNotes());
        populateCategoryDropdown();
    }

    private void populateCategoryDropdown() {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TransactionDetails.this,
                R.layout.dropdown_item, categoryStrings);

        categoryDropdown.setAdapter(adapter);
        if (categoryName != null && categoryID > -1) {
            categoryDropdown.setSelection(adapter.getPosition(categoryName));
        }
    }

    private void fetchUiElements() {
        payeeEdit = findViewById(R.id.payeeEdit);
        amountEdit = findViewById(R.id.amountEdit);
        dateView = findViewById(R.id.dateView);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        notesEdit = findViewById(R.id.notesEdit);
        statusDropdown = findViewById(R.id.statusDropdown);
    }
}