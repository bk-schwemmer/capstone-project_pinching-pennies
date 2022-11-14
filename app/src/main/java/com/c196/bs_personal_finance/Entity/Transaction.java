package com.c196.bs_personal_finance.Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.DecimalFormat;

@Entity(tableName = "transactions",
        foreignKeys =
        @ForeignKey(entity = Account.class,
                parentColumns = "accountID",
                childColumns = "accountID"))

public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private long transactionID;

    private long accountID;
    private double amount;
    private String payee;
    private String date;
    private Status status;
    private String notes;
    private long category;

    public enum Status { PENDING, ESTIMATE, FUTURE, RECONCILED }

    public Transaction(long accountID, double amount, String payee, String date, Status status, String notes, long category) {
        this.accountID = accountID;
        this.amount = amount;
        this.payee = payee;
        this.date = date;
        this.status = status;
        this.notes = notes;
        this.category = category;
    }

    @Ignore
    public Transaction(long transactionID, long accountID, double amount, String payee, String date, Status status, String notes, long category) {
        this.transactionID = transactionID;
        this.accountID = accountID;
        this.amount = amount;
        this.payee = payee;
        this.date = date;
        this.status = status;
        this.notes = notes;
        this.category = category;
    }

    public long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(long transactionID) {
        this.transactionID = transactionID;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountString() {
        DecimalFormat df = new DecimalFormat("#,###.##");
        String formattedBalance;
        if (amount < 0) {
            amount = amount * -1;
            formattedBalance = "($" + df.format(amount) + ")";
        } else {
            formattedBalance = "$" + df.format(amount);
        }
        return formattedBalance;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID=" + transactionID +
                ", accountID=" + accountID +
                ", amount=" + amount +
                ", payee='" + payee + '\'' +
                '}';
    }
}
