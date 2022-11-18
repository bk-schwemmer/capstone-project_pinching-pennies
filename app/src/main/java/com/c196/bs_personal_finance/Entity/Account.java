package com.c196.bs_personal_finance.Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.DecimalFormat;

@Entity(tableName = "accounts",
        foreignKeys =
        @ForeignKey(entity = User.class,
        parentColumns = "userID",
        childColumns = "userID"))

public class Account {
    @PrimaryKey(autoGenerate = true)
    private long accountID;

    private long userID;
    private String accountName;
    private AccountType type;
    private double currentBalance;

    public enum AccountType { Cash, Checking, Savings, Fund, Debt, Credit, Payment}

    public Account(long userID, String accountName, AccountType type, double currentBalance) {
        this.userID = userID;
        this.accountName = accountName;
        this.type = type;
        this.currentBalance = currentBalance;
    }

    @Ignore
    public Account(long accountID, long userID, String accountName, AccountType type, double currentBalance) {
        this.accountID = accountID;
        this.userID = userID;
        this.accountName = accountName;
        this.type = type;
        this.currentBalance = currentBalance;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public String getCurrentBalanceString() {
        DecimalFormat df = new DecimalFormat("#,###.##");
//        String formattedBalance;
//        if (currentBalance < 0) {
//            currentBalance = currentBalance * -1;
//            formattedBalance = "($" + df.format(currentBalance) + ")";
//        } else {
//            formattedBalance = "$" + df.format(currentBalance);
//        }
//        return formattedBalance;
        return df.format(currentBalance);
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountID=" + accountID +
                ", userID=" + userID +
                ", type=" + type +
                ", currentBalance=" + currentBalance +
                '}';
    }

    public static String typeToString(AccountType accountType) {
        switch (accountType) {
            case Cash:
                return "Cash";
            case Checking:
                return "Checking";
            case Savings:
                return "Savings";
            case Fund:
                return "Fund";
            case Credit:
                return "Credit";
            case Debt:
                return "Debt";
            case Payment:
                return "Payment";

            default:
                return "";
        }
    }

    public static AccountType stringToType(String typeString) {
        switch (typeString) {
            case "Cash":
                return AccountType.Cash;
            case "Checking":
                return AccountType.Checking;
            case "Savings":
                return AccountType.Savings;
            case "Fund":
                return AccountType.Fund;
            case "Credit":
                return AccountType.Credit;
            case "Debt":
                return AccountType.Debt;
            case "Payment":
                return AccountType.Payment;

            default:
                return null;
        }
    }
}
