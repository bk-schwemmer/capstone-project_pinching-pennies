package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions extends AppCompatActivity {

    public static final String SELECTED_TRANSACTION_ID = "selectedTransactionID";

    private Repository repo;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    private long currentUserID;
    private long currentAccountID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_transactions);
        setTitle("Transactions");

        repo = new Repository(getApplication());
        fetchUiElements();
        populateTransactionsList();
    }

    private void populateTransactionsList() {
//        currentUserID = getIntent().getLongExtra(Login.CURRENT_USER_ID, 0);
        currentAccountID = getIntent().getLongExtra(Accounts.CURRENT_ACCOUNT_ID, 0);
        currentUserID = repo.getAccountByID(currentAccountID).getUserID();

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

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition),
                        Toast.LENGTH_LONG).show();

                String[] parsedChild = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).split("_");
                long transactionID = Long.parseLong(parsedChild[0]);
                Intent intent = new Intent(Transactions.this, TransactionDetails.class);
                intent.putExtra(SELECTED_TRANSACTION_ID, transactionID);
                startActivity(intent);

                return false;
            }
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
    }
}