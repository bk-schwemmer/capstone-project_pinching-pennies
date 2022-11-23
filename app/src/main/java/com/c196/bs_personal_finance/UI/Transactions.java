package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SearchView;
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

public class Transactions extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String SELECTED_TRANSACTION_ID = "selectedTransactionID";
    public static final String TRANSACTION_PURPOSE = "transactionPurpose";

    private Repository repo;
    private ExpandableListView expandableList;
    private ExpandableListAdapter expandableListAdapter;
    private List<Transaction> transactions;
    private List<String> datesList;
    private HashMap<String, List<String>> transactionMap;
    private long currentUserID;
    private long currentAccountID = 0;

    private TextView noTransactionsView;
    private TextView transactionHeading;
    private SearchView search;
    private FloatingActionButton addTransactionButton;

    // CLICK LISTENERS
    private final View.OnClickListener addTransaction = view -> {
        Intent intent = new Intent(Transactions.this, TransactionDetails.class);
        intent.putExtra(Accounts.CURRENT_ACCOUNT_ID, currentAccountID);
        intent.putExtra(TRANSACTION_PURPOSE, getString(R.string.add));
        startActivity(intent);
    };

    private final View.OnClickListener onSearch = view -> {
        Intent intent = new Intent(Transactions.this, TransactionDetails.class);
        intent.putExtra(Accounts.CURRENT_ACCOUNT_ID, currentAccountID);
        intent.putExtra(TRANSACTION_PURPOSE, getString(R.string.add));
        startActivity(intent);
    };

    private final ExpandableListView.OnChildClickListener transactionSelected =
            ((parent, view, groupPosition, childPosition, id) -> {
        Toast.makeText(
                getApplicationContext(),
                transactionMap.get(datesList.get(groupPosition)).get(childPosition),
                Toast.LENGTH_LONG).show();

        String[] parsedChild = transactionMap.get(datesList.get(groupPosition)).get(childPosition).split("_");
        long transactionID = Long.parseLong(parsedChild[0]);
        Intent intent = new Intent(Transactions.this, TransactionDetails.class);
        intent.putExtra(SELECTED_TRANSACTION_ID, transactionID);
        intent.putExtra(TRANSACTION_PURPOSE, getString(R.string.modify));
        startActivity(intent);

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_transactions);

        repo = new Repository(getApplication());
        fetchUiElements();
        populatePageDetails();

        // Set up search functionality
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);

        if (transactionsInAccount()) {
            displayList();
            expandAll();
        } else {
            noTransactionsView.setVisibility(View.VISIBLE);
            expandableList.setVisibility(View.GONE);
            transactionHeading.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
        }

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
            case R.id.accounts:
                Intent toAccountsScreen = new Intent(Transactions.this, Accounts.class);
                toAccountsScreen.putExtra(UserLogin.CURRENT_USER_ID, currentUserID);
                startActivity(toAccountsScreen);
                return true;

            case R.id.reports:
                Intent toReports = new Intent(Transactions.this, Reports.class);
                toReports.putExtra(UserLogin.CURRENT_USER_ID, currentUserID);
                startActivity(toReports);
                return true;

            case R.id.logout:
                Intent toLoginScreen = new Intent(Transactions.this, UserLogin.class);
                startActivity(toLoginScreen);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchUiElements() {
        expandableList = findViewById(R.id.transactionsExpandableList);
        addTransactionButton = findViewById(R.id.addTransactionButton);
        noTransactionsView = findViewById(R.id.noTransactions);
        search = (SearchView) findViewById(R.id.transactionSearch);
        transactionHeading = findViewById(R.id.transactionHeading);
    }

    private void populatePageDetails() {
        currentAccountID = getIntent().getLongExtra(Accounts.CURRENT_ACCOUNT_ID, 0);
        currentUserID = repo.getAccountByID(currentAccountID).getUserID();

        // Set page title
        setTitle(getString(R.string.transactions));

        // Set subtitle
        StringBuilder heading = new StringBuilder();
        heading.append(repo.getAccountByID(currentAccountID).getAccountName());
        heading.append(" ").append(getString(R.string.account));
        transactionHeading.setText(heading);
    }

    private boolean transactionsInAccount() {
        transactionMap = new HashMap<>();
        if (currentAccountID == 0) {
            transactions = repo.getTransactionsByUser(currentUserID);
        } else {
            transactions = repo.getTransactionByAccount(currentAccountID);
        }

        return transactions.size() != 0;
    }

    private void expandAll() {
        int count = expandableListAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableList.expandGroup(i);
        }
    }

    private void displayList() {
        createList();
        expandableListAdapter = new ExpandableListAdapter(this, datesList, transactionMap);
        expandableList.setAdapter(expandableListAdapter);

        expandableList.setOnGroupExpandListener(groupPosition ->
                Toast.makeText(getApplicationContext(),
                        datesList.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show());

        expandableList.setOnGroupCollapseListener(groupPosition ->
                Toast.makeText(getApplicationContext(),
                        datesList.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show());

        expandableList.setOnChildClickListener(transactionSelected);
    }

    private void createList() {

        List<String> transactionDetails = new ArrayList<>();
        int counter = 0;
        String transactionDate = transactions.get(0).getDate();
        for (Transaction transaction : transactions) {
            counter++;

            // If the current date doesn't match the previous date, submit the list to the hashmap then start the list over
            if (!transaction.getDate().equals(transactionDate)) {
                transactionMap.put(transactionDate, transactionDetails);
                transactionDate = transaction.getDate();
                transactionDetails = new ArrayList<>();
            }
            transactionDetails.add(transaction.getTransactionID() + "_" + transaction.getPayee() + "_" + transaction.getAmount());

            if (counter == transactions.size()) {
                transactionMap.put(transactionDate, transactionDetails);
            }
        }

        datesList = new ArrayList<>(transactionMap.keySet());
        datesList.sort(Collections.reverseOrder());
    }

    @Override
    public boolean onClose() {
        expandableListAdapter.filterData("");
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        expandableListAdapter.filterData(newText);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        expandableListAdapter.filterData(query);
        expandAll();
        return false;
    }
}