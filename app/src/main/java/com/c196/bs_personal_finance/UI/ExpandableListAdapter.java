package com.c196.bs_personal_finance.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.c196.bs_personal_finance.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private List<String> dateList;
    private List<String> originalDateList;
    private HashMap<String, List<String>> transactionsList;
    private HashMap<String, List<String>> originalTransactionsList;

    public ExpandableListAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.originalDateList = new ArrayList<>();
        this.originalDateList.addAll(expandableListTitle);
        this.dateList = new ArrayList<>();
        this.dateList.addAll(expandableListTitle);

        this.originalTransactionsList = new HashMap<>();
        this.originalTransactionsList.putAll(expandableListDetail);
        this.transactionsList = new HashMap<>();
        this.transactionsList.putAll(expandableListDetail);
    }

    @Override
    public int getGroupCount() {
        return this.dateList.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        try {
            return this.transactionsList
                    .get(this.dateList.get(listPosition))
                    .size();
        } catch (NullPointerException e) {
            Log.e("ExpandableListAdapter", "Unable to get size of dateList at position " + listPosition);
            return -1;
        }
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.dateList.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
         try {
            return this.transactionsList
                    .get(this.dateList
                    .get(listPosition))
                    .get(expandedListPosition);
        } catch (NullPointerException e) {
            Log.e("ExpandableListAdapter", "Unable to get child at position " + expandedListPosition);
            return null;
        }
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_parent, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.parentTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String transactionText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_child, null);
        }

        TextView transactionTitleView = convertView.findViewById(R.id.childTitle);
        TextView transactionAmountView = convertView.findViewById(R.id.childAmount);
        String[] parsedTransaction = transactionText.split("_");
        transactionTitleView.setText(parsedTransaction[1]);

        if (parsedTransaction.length > 2) {
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            String amountString = parsedTransaction[2];
            double amount;

            if (TransactionDetails.validCurrency(amountString)) {
                amount = Double.parseDouble(amountString);
                amountString = nf.format(amount);
            }
            transactionAmountView.setText(amountString);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public void filterData(String query) {
        // Followed Tutorial at https://www.loginworks.com/blogs/work-expandablelistview-android/ guide on implementation
        query = query.toLowerCase();
        dateList.clear();
        transactionsList.clear();

        if (query.isEmpty()) {
            dateList.addAll(originalDateList);
            transactionsList.putAll(originalTransactionsList);
        } else {
            for (Map.Entry<String, List<String>> transactionEntry : originalTransactionsList.entrySet()) {
                List<String> transactions = new ArrayList<>();
                for (String value : transactionEntry.getValue()) {
                    String[] transactionData = value.split("_", 2);
                    if (transactionData.length > 1) {
                        if ((transactionData[1].toLowerCase()).contains(query)) {
                            transactions.add(value);
                        }
                    }
                }
                if (transactions.size() > 0) {
                    dateList.add(transactionEntry.getKey());
                    transactionsList.put(transactionEntry.getKey(), transactions);
                }
            }
        }
        Log.v("TransactionListAdapter", "Transaction List Size: " + transactionsList.size());
        notifyDataSetChanged();
    }
}
