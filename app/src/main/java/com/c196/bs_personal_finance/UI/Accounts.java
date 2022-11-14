package com.c196.bs_personal_finance.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Accounts extends AppCompatActivity {

    public static final String CURRENT_ACCOUNT_ID = "currentAccountID";

    private Repository repo;
    private AccountAdapter mAssetsAccountAdapter;
    private AccountAdapter mLiabilitiesAccountAdapter;
    private RecyclerView mAssetRecyclerView;
    private RecyclerView mLiabilityRecyclerView;
    private long currentUserID;
    private FloatingActionButton addAccountButton;

    // CLICK LISTENERS


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_accounts);

        repo = new Repository(getApplication());
        fetchUiElements();
        populateAccountsRecycler();


        addAccountButton.setOnClickListener(view -> {
            AddAccountFragment addAcctFrag = new AddAccountFragment();
            addAcctFrag.show(getSupportFragmentManager(), "Add Account Fragment");
        });

    }

    public boolean addAccount(long userID, String accountName, Account.AccountType accountType,
                            double currentBalance) {

        // Search current accounts for duplicate names
        List<Account> allUserAccounts = repo.getAccountsByUser(userID);
        for (Account account: allUserAccounts) {
            if (account.getAccountName().equalsIgnoreCase(accountName)) {
                Toast.makeText(
                        Accounts.this,
                        "An account with this name already exists.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Create new account and attempt to add to database
        Account accountToAdd = new Account(userID,accountName, accountType, currentBalance);
        long newAccountID = repo.insert(accountToAdd);
        if (newAccountID > 0) {
            return true;
        } else {
            Toast.makeText(
                    Accounts.this,
                    "Unable to add account. Please try again",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void populateAccountsRecycler() {

        currentUserID = getIntent().getLongExtra(Login.CURRENT_USER_ID, 0);
        List<Account> allUserAccounts = repo.getAccountsByUser(currentUserID);
        List<Account> assetAccounts = new ArrayList<>();
        List<Account> liabilityAccounts = new ArrayList<>();
        List<Account> unsortedAccounts = new ArrayList<>();
        for (Account account: allUserAccounts) {
            if (sortType(account.getType()).equals("Asset")) {
                assetAccounts.add(account);
            } else if (sortType(account.getType()).equals("Liability")) {
                liabilityAccounts.add(account);
            } else {
                unsortedAccounts.add(account);
            }
        }

        if (unsortedAccounts.size() > 0) {
            Toast.makeText(
                    Accounts.this,
                    "There is/are " + unsortedAccounts.size() + " unsorted accounts",
                    Toast.LENGTH_LONG).show();
        }

        // TODO: PUT IN WORKER THREAD
        // Load terms
        RecyclerView.LayoutManager assetLayoutManager =
                new GridLayoutManager(getApplicationContext(), 1);
        RecyclerView.LayoutManager liabilityLayoutManager =
                new GridLayoutManager(getApplicationContext(), 1);
        mAssetRecyclerView.setLayoutManager(assetLayoutManager);
        mLiabilityRecyclerView.setLayoutManager(liabilityLayoutManager);
        mAssetsAccountAdapter = new AccountAdapter(assetAccounts);
        mLiabilitiesAccountAdapter = new AccountAdapter(liabilityAccounts);
        mAssetRecyclerView.setAdapter(mAssetsAccountAdapter);
        mLiabilityRecyclerView.setAdapter(mLiabilitiesAccountAdapter);

    }

    private String sortType(Account.AccountType type) {
        switch (type) {
            case CASH:
            case CHECKING:
            case SAVINGS:
            case FUND:
                return "Asset";
            case DEBT:
            case CREDIT:
            case PAYMENT:
                return "Liability";

            default:
                return "";
        }
    }

    private class AccountHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Account mAccount;
        private final TextView mNameView;
        private final TextView mBalanceView;

        public AccountHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recyclerview_account_card, parent, false));
            itemView.setOnClickListener(this);
            mNameView = itemView.findViewById(R.id.accountNameCardText);
            mBalanceView = itemView.findViewById(R.id.accountBalanceCardText);
        }

        public void bind(Account account, int position) {
            mAccount = account;
            mNameView.setText(account.getAccountName());
            mBalanceView.setText(account.getCurrentBalanceString());

//            mTextView.setBackgroundColor(mTermColors[cardAdder % mTermColors.length]);
//            cardAdder++;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Accounts.this, Transactions.class);
            intent.putExtra(Login.CURRENT_USER_ID, mAccount.getUserID());
            intent.putExtra(CURRENT_ACCOUNT_ID, mAccount.getAccountID());
            startActivity(intent);
        }
    }

    private class AccountAdapter extends RecyclerView.Adapter<AccountHolder> {

        private final List<Account> mAccountList;

        public AccountAdapter(List<Account> accounts) {
            mAccountList = accounts;
        }

        @Override
        public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new AccountHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
            holder.bind(mAccountList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mAccountList.size();
        }
    }

    private void fetchUiElements() {
        mAssetRecyclerView = findViewById(R.id.assetAccountsRecycler);
        mLiabilityRecyclerView = findViewById(R.id.liabilityAccountsRecycler);

        addAccountButton = findViewById(R.id.addAccountButton);
    }

}