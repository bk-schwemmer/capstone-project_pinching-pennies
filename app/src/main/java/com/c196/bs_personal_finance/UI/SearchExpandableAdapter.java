package com.c196.bs_personal_finance.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.c196.bs_personal_finance.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Followed guidance at https://stackoverflow.com/questions/40865182/how-to-implement-filter-in-expandablelistview-with-hashmap

public class SearchExpandableAdapter extends BaseExpandableListAdapter implements Filterable {

    private Context context;
    ItemFilter mFilter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    public SearchExpandableAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail
                .get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail
                .get(this.expandableListTitle
                        .get(listPosition))
                .get(expandedListPosition);
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

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_child, null);
        }

        TextView expandedListChildTitleView = convertView.findViewById(R.id.childTitle);
        TextView expandedListChildAmountView = convertView.findViewById(R.id.childAmount);

        String[] parsedChild = expandedListText.split("_");

        expandedListChildTitleView.setText(parsedChild[1]);
        if (parsedChild.length > 2) {
            NumberFormat nf = NumberFormat.getCurrencyInstance();

            String amountString = parsedChild[2];

            double amount;
            if (TransactionDetails.validDouble(amountString)) {
                amount = Double.parseDouble(amountString);
                amountString = nf.format(amount);
            }
            expandedListChildAmountView.setText(amountString);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }
        return mFilter;

    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                HashMap<String, List<String>> filterList = new HashMap<>();

//                for (int i = 0; i < expandableListDetail.size(); i++) {
                for (Map.Entry<String, List<String>> entry: expandableListDetail.entrySet()) {
                    List<String> transactions = new ArrayList<>();
                    for (String value: entry.getValue()) {
                        if ((value.toLowerCase()).contains(constraint.toString().toUpperCase())) {
//                            String[] data = value.split("_") ;
                            transactions.add(value);
                        }
                    }
                    if (transactions.size() > 0) {
                        filterList.put(entry.getKey(), transactions);
                    }
                }
            }


            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        }
    }
}