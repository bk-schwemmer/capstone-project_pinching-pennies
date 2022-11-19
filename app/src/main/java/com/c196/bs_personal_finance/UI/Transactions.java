package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions extends AppCompatActivity {

    public static final String SELECTED_TRANSACTION_ID = "selectedTransactionID";
    public static final String TRANSACTION_PURPOSE = "transactionPurpose";

    private Repository repo;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    private long currentUserID;
    private long currentAccountID = 0;

    private TextView noTransactionsView;
    private TextView transactionHeading;
    private FloatingActionButton addTransactionButton;

    // CLICK LISTENERS
    private final View.OnClickListener addTransaction = view -> {
        Intent intent = new Intent(Transactions.this, TransactionDetails.class);
        intent.putExtra(Accounts.CURRENT_ACCOUNT_ID, currentAccountID);
        intent.putExtra(TRANSACTION_PURPOSE, getString(R.string.add));
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_transactions);

        repo = new Repository(getApplication());
        fetchUiElements();
        populateTransactionsList();

        addTransactionButton.setOnClickListener(addTransaction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transactions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent toLoginScreen = new Intent(Transactions.this, UserLogin.class);
                startActivity(toLoginScreen);
                return true;

            case R.id.accounts:
                Intent toAccountsScreen = new Intent(Transactions.this, Accounts.class);
                toAccountsScreen.putExtra(UserLogin.CURRENT_USER_ID, currentUserID);
                startActivity(toAccountsScreen);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateTransactionsList() {
        currentAccountID = getIntent().getLongExtra(Accounts.CURRENT_ACCOUNT_ID, 0);
        currentUserID = repo.getAccountByID(currentAccountID).getUserID();

        setTitle(repo.getAccountByID(currentAccountID).getAccountName());

        createTransactionHashMap();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());

        // TODO Fix this once I change the keys to be dates instead of strings
        expandableListTitle.sort(Collections.reverseOrder());
        expandableListAdapter = new ExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        // Expand all groups by default
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }

        expandableListView.setOnGroupExpandListener(groupPosition ->
                Toast.makeText(getApplicationContext(),
                expandableListTitle.get(groupPosition) + " List Expanded.",
                Toast.LENGTH_SHORT).show());

        expandableListView.setOnGroupCollapseListener(groupPosition ->
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show());

        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            Toast.makeText(
                    getApplicationContext(),
                    expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition),
                    Toast.LENGTH_LONG).show();

            String[] parsedChild = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).split("_");
            long transactionID = Long.parseLong(parsedChild[0]);
            Intent intent = new Intent(Transactions.this, TransactionDetails.class);
            intent.putExtra(SELECTED_TRANSACTION_ID, transactionID);
            intent.putExtra(TRANSACTION_PURPOSE, getString(R.string.modify));
            startActivity(intent);

            return false;
        });
    }

    private void createTransactionHashMap() {
        expandableListDetail = new HashMap<>();
        List<Transaction> transactions;
        List<String> transactionDetails = new ArrayList<>();
        if (currentAccountID == 0) {
            transactions = repo.getTransactionsByUser(currentUserID);
        } else {
            transactions = repo.getTransactionByAccount(currentAccountID);
        }

        if (transactions.size() == 0) {
            noTransactionsView.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
            transactionHeading.setVisibility(View.GONE);
            return;
        }


        // TODO DELETE THIS
        int i = 1;
        for (Transaction transaction : transactions) {
            System.out.println(i + ": " + transaction.getTransactionID() + " | " +
                    transaction.getPayee() + " | " +
                    transaction.getAmount() + " | " +
                    transaction.getCategory());
            i++;
        }

        int counter = 0;
        String transactionDate = transactions.get(0).getDate();
        for (Transaction transaction : transactions) {
            counter++;

            // TODO DELETE
            System.out.println("TransactionDate = " + transactionDate);
            System.out.println("CurrentDate = " + transaction.getDate());

            // IF the current date doesn't match the previous date, submit the list to the hashmap then start the list over
            if (!transaction.getDate().equals(transactionDate)) {
                expandableListDetail.put(transactionDate, transactionDetails);
                transactionDate = transaction.getDate();
                transactionDetails = new ArrayList<>();
            }
            transactionDetails.add(transaction.getTransactionID() + "_" + transaction.getPayee() + "_" + transaction.getAmount());

            if (counter == transactions.size()) {
                expandableListDetail.put(transactionDate, transactionDetails);
            }
        }

        // TODO DELETE
        int index = 1;
        for (Map.Entry<String, List<String>> entry: expandableListDetail.entrySet()) {
            System.out.println("#" + index + ": " + entry.getKey() + " | " + entry.getValue());
            index++;
        }
    }

    private void fetchUiElements() {
        expandableListView = findViewById(R.id.transactionsExpandableList);
        addTransactionButton = findViewById(R.id.addTransactionButton);
        noTransactionsView = findViewById(R.id.noTransactions);
        transactionHeading = findViewById(R.id.transactionHeading);
    }
}