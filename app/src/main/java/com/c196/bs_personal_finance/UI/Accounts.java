package com.c196.bs_personal_finance.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Account;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


// Followed answer guide here to implement Context Menu in RecyclerView
// https://stackoverflow.com/questions/26466877/how-to-create-context-menu-for-recyclerview

public class Accounts extends AppCompatActivity implements AccountDeleteFragment.OnAccountDeletedListener {

    public static final String CURRENT_ACCOUNT_ID = "currentAccountID";
    public static final String ACCOUNT_PURPOSE = "accountPurpose";

    private Repository repo;
    private AccountAdapter mAssetsAccountAdapter;
    private AccountAdapter mLiabilitiesAccountAdapter;
    private Account selectedAccount;
    private long currentUserID;
    private int mCurrentItemPosition;

    private LinearLayout assetLayout;
    private LinearLayout liabilityLayout;
    private TextView noAccounts;
    private RecyclerView mAssetRecyclerView;
    private RecyclerView mLiabilityRecyclerView;
    private FloatingActionButton addAccountButton;
    private ProgressBar deletingProgress;
    private CardView mAccountCard;

    private Choice selectedList;

    private enum Choice { ASSET, LIABILITY }

    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position);
    }

    // CLICK LISTENERS
    private final View.OnClickListener addAccount = view -> {
        Intent intent = new Intent(Accounts.this, AccountDetails.class);
        intent.putExtra(UserLogin.CURRENT_USER_ID, currentUserID);
        intent.putExtra(ACCOUNT_PURPOSE, getString(R.string.add));
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_accounts);
        setTitle(R.string.accounts);

        repo = new Repository(getApplication());
        fetchUiElements();
        populateAccountsRecycler();

        addAccountButton.setOnClickListener(addAccount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accounts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.userDetails:
                Intent toUserDetails = new Intent(Accounts.this, UserDetails.class);
                toUserDetails.putExtra(UserLogin.CURRENT_USER_ID, currentUserID);
                toUserDetails.putExtra(UserLogin.PURPOSE, getString(R.string.modify));
                startActivity(toUserDetails);
                return true;

            case R.id.reports:
                Intent toReports = new Intent(Accounts.this, Reports.class);
                toReports.putExtra(UserLogin.CURRENT_USER_ID, currentUserID);
                startActivity(toReports);
                return true;

            case R.id.logout:
                Intent toLoginScreen = new Intent(Accounts.this, UserLogin.class);
                startActivity(toLoginScreen);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchUiElements() {
        // Recyclers
        mAssetRecyclerView = findViewById(R.id.assetAccountsRecycler);
        mLiabilityRecyclerView = findViewById(R.id.liabilityAccountsRecycler);

        // Layouts
        assetLayout = findViewById(R.id.assetLayout);
        liabilityLayout = findViewById(R.id.liabilityLayout);
        noAccounts = findViewById(R.id.noAccounts);

        // Progress
        deletingProgress = findViewById(R.id.deletingProgressBar);

        // Button
        addAccountButton = findViewById(R.id.addAccountButton);
    }

    private void populateAccountsRecycler() {

        currentUserID = getIntent().getLongExtra(UserLogin.CURRENT_USER_ID, 0);
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

        if (assetAccounts.size() == 0) {
            assetLayout.setVisibility(View.GONE);
        }
        if (liabilityAccounts.size() == 0) {
            liabilityLayout.setVisibility(View.GONE);
            assetLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if ((assetAccounts.size() == 0) && (liabilityAccounts.size() == 0)) {
            noAccounts.setVisibility(View.VISIBLE);
        }

        // TODO DELETE
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

        Thread thread = new Thread(() -> {
            mAssetsAccountAdapter = new AccountAdapter(assetAccounts);
            mLiabilitiesAccountAdapter = new AccountAdapter(liabilityAccounts);

            Accounts.this.runOnUiThread(() -> {
                mAssetRecyclerView.setAdapter(mAssetsAccountAdapter);
                mLiabilityRecyclerView.setAdapter(mLiabilitiesAccountAdapter);
            });
            registerForContextMenu(mAssetRecyclerView);
            registerForContextMenu(mLiabilityRecyclerView);

            mAssetsAccountAdapter.setOnLongItemClickListener((v, position) -> {
                mCurrentItemPosition = position;
                selectedList = Choice.ASSET;
            });

            mLiabilitiesAccountAdapter.setOnLongItemClickListener((v, position) -> {
                mCurrentItemPosition = position;
                selectedList = Choice.LIABILITY;
            });
        });
        thread.start();
    }

    private String sortType(Account.AccountType type) {
        switch (type) {
            case Cash:
            case Checking:
            case Savings:
            case Fund:
                return "Asset";
            case Debt:
            case Credit:
            case Payment:
                return "Liability";

            default:
                return "";
        }
    }

    private class AccountHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Account mAccount;
        private final TextView mNameView;
        private final TextView mBalanceView;

        public AccountHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recyclerview_account_card, parent, false));
            itemView.setOnClickListener(this);
            mAccountCard = itemView.findViewById(R.id.accountCardView);
            System.out.println(parent.toString());
//            String s = "asset";
            if ((parent.toString()).contains("asset")) {
                mAccountCard.setCardBackgroundColor(getColor(R.color.light_asset_green));
            } else {
                mAccountCard.setCardBackgroundColor(getColor(R.color.light_liability_red));
            }
            mNameView = itemView.findViewById(R.id.accountNameCardText);
            mBalanceView = itemView.findViewById(R.id.accountBalanceCardText);
        }

        public void bind(Account account, int position) {
            mAccount = account;
            mNameView.setText(account.getAccountName());
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            double balance = account.getCurrentBalance();
            mBalanceView.setText(nf.format(balance));

//            mTextView.setBackgroundColor(mTermColors[cardAdder % mTermColors.length]);
//            cardAdder++;
        }

        @Override
        public void onClick(View view) {

            selectedAccount = mAccount;

            Intent intent = new Intent(Accounts.this, Transactions.class);
            intent.putExtra(UserLogin.CURRENT_USER_ID, mAccount.getUserID());
            intent.putExtra(CURRENT_ACCOUNT_ID, mAccount.getAccountID());
            startActivity(intent);
        }
    }

    private class AccountAdapter extends RecyclerView.Adapter<AccountHolder> {

        private final List<Account> mAccountList;
        private onLongItemClickListener mOnLongItemClickListener;


        public AccountAdapter(List<Account> accounts) {
            mAccountList = accounts;
        }

        public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
            mOnLongItemClickListener = onLongItemClickListener;
        }

        @Override
        public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new AccountHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
            holder.bind(mAccountList.get(position), position);
            holder.itemView.setOnLongClickListener(view -> {
                if (mOnLongItemClickListener != null) {
                    mOnLongItemClickListener.ItemLongClicked(view, holder.getBindingAdapterPosition());
                }
                return false;
            });
        }

        @Override
        public int getItemCount() {
            return mAccountList.size();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (selectedList.equals(Choice.ASSET)) {
            selectedAccount = mAssetsAccountAdapter.mAccountList.get(mCurrentItemPosition);
        } else {
            selectedAccount = mLiabilitiesAccountAdapter.mAccountList.get(mCurrentItemPosition);
        }

        switch (item.getItemId()) {
            case R.id.accountDetails:
                Intent intent = new Intent(Accounts.this, AccountDetails.class);
                intent.putExtra(UserLogin.CURRENT_USER_ID, selectedAccount.getUserID());
                intent.putExtra(CURRENT_ACCOUNT_ID, selectedAccount.getAccountID());
                intent.putExtra(ACCOUNT_PURPOSE, getString(R.string.modify));
                startActivity(intent);
                return false;

            case R.id.deleteAccount:
                Thread deleteTermThread = new Thread(() -> {

                    List<Transaction> transactionsInAccount = repo.getTransactionByAccount(selectedAccount.getAccountID());
                    if (transactionsInAccount.size() == 0) {
                        deleteAccount(selectedAccount);
                    } else {
                        Accounts.this.runOnUiThread(() -> {
                            FragmentManager manager = getSupportFragmentManager();
                            AccountDeleteFragment dialog = new AccountDeleteFragment();
                            dialog.show(manager, "warningDialog");
                            System.out.println("Unable to delete Account. There are transactions assigned to this account.");
                            Toast.makeText(
                                    Accounts.this,
                                    "This term has courses",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                deleteTermThread.start();
                return false;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onDeleteOverride() {
        deletingProgress.setVisibility(View.VISIBLE);
        Thread thread = new Thread(() -> {
            if (deleteTransactionsInAccount(selectedAccount)) {
                deleteAccount(selectedAccount);
            }
        });
        thread.start();
    }

    public void deleteAccount(Account account) {
        Accounts.this.runOnUiThread(() -> {
            deletingProgress.setVisibility(View.VISIBLE);
        });

        if (repo.delete(account) < 1) {
            Accounts.this.runOnUiThread(() -> {
                Toast.makeText(this, "Account Deletion Failed", Toast.LENGTH_SHORT).show();
                deletingProgress.setVisibility(View.GONE);
            });
        } else {
            Accounts.this.runOnUiThread(() -> {
                Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Accounts.this, Accounts.class);
                intent.putExtra(UserLogin.CURRENT_USER_ID, currentUserID);
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
}