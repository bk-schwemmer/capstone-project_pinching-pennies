package com.c196.bs_personal_finance.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.c196.bs_personal_finance.Database.Repository;
import com.c196.bs_personal_finance.Entity.Category;
import com.c196.bs_personal_finance.Entity.Transaction;
import com.c196.bs_personal_finance.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Reports extends AppCompatActivity implements DatePickerFragment.ReturnTimeframeListener {

    // MEMBERS
    public static ReportType reportType;
    public static int selectedPicker;
    private Repository repo;
    private long currentUserID;
    private List<Category> allCategories;
    private String[] categories;
    private boolean[] categorySelections;
    private int rowNum;

    // VIEWS
    private Spinner typeDropdown;
    private TextView categoryDropdown;
    private Button startPicker;
    private Button endPicker;
    private TextView reportNotReady;
    private TableLayout reportTable;

    // Dates
    final Calendar calendar = Calendar.getInstance();
    final String dayFormat = "MM/dd/yyyy";
    final String monthFormat = "MM/yyyy";
    final String yearFormat = "yyyy";
    final SimpleDateFormat sdf = new SimpleDateFormat(dayFormat, Locale.US);
    final SimpleDateFormat mdf = new SimpleDateFormat(monthFormat, Locale.US);
    final SimpleDateFormat ydf = new SimpleDateFormat(yearFormat, Locale.US);
    final String currentDate = sdf.format(new Date());


    public enum ReportType { MoM, YoY }

    private final View.OnClickListener pickCategory = view -> {

        boolean[] tempSelected = new boolean[categorySelections.length];
        System.arraycopy(categorySelections, 0, tempSelected, 0, categorySelections.length);

        AlertDialog.Builder builder = new AlertDialog.Builder(Reports.this);
        builder.setTitle(getString(R.string.select_categories));
        builder.setCancelable(false);
        builder.setMultiChoiceItems(categories,
                categorySelections,
                        (DialogInterface.OnMultiChoiceClickListener) (dialogInterface, itemIndex, selected) -> {
        });
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                categoryDropdown.setText(R.string.change_categories);
                generateReport();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                categorySelections = tempSelected;
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton(R.string.clear_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < categorySelections.length; j++) {
                    categorySelections[j] = false;
                    categoryDropdown.setText("");
                }
                generateReport();
            }
        });
        builder.show();
    };

    private final View.OnClickListener pickDate = view -> {
        selectedPicker = view.getId();

        try {
            calendar.setTime(sdf.parse(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Reports", "Parse Exception");
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e("Reports", "Unable to retrieve date");
        }

        DialogFragment datePickerFrag = new DatePickerFragment();
        datePickerFrag.show(getSupportFragmentManager(), "datePicker");
    };

    private final AdapterView.OnItemSelectedListener typeSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected (AdapterView <?> parentView, View selectedItemView,
        int position, long id){
        if (typeDropdown.getSelectedItem().equals("MoM")) {
            reportType = ReportType.MoM;
            startPicker.setText(R.string.set_month);
            endPicker.setText(R.string.set_month);
        } else if (typeDropdown.getSelectedItem().equals("YoY")) {
            reportType = ReportType.YoY;
            startPicker.setText(R.string.set_year);
            endPicker.setText(R.string.set_year);
        }
            generateReport();
    }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_reports);
        setTitle(getString(R.string.reports));

        repo = new Repository(getApplication());
        currentUserID = getIntent().getLongExtra(UserLogin.CURRENT_USER_ID, 0);

        fetchUiElements();
        populateTypeDropdown();
        populateCategories();

        // Set click listeners
        categoryDropdown.setOnClickListener(pickCategory);
        typeDropdown.setOnItemSelectedListener(typeSelected);
        startPicker.setOnClickListener(pickDate);
        endPicker.setOnClickListener(pickDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reports, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accounts:
                Intent toAccounts = new Intent(Reports.this, Accounts.class);
                toAccounts.putExtra(UserLogin.CURRENT_USER_ID, currentUserID);
                startActivity(toAccounts);
                return true;

            case R.id.logout:
                Intent toLoginScreen = new Intent(Reports.this, UserLogin.class);
                startActivity(toLoginScreen);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchUiElements() {

        // Text
        reportNotReady = findViewById(R.id.reportNotReady);
        reportTable = (TableLayout) findViewById(R.id.reportTable);

        // Dropdown Lists
        typeDropdown = findViewById(R.id.reportTypeSpinner);
        categoryDropdown = findViewById(R.id.categoryDropdown);

        // Buttons
        startPicker = findViewById(R.id.timeframeStartPicker);
        endPicker = findViewById(R.id.timeframeEndPicker);
    }

    private void populateCategories() {

        Thread categoryDropdownThread = new Thread(() -> {
            allCategories = repo.getAllCategories();
            categorySelections = new boolean[allCategories.size()];
            Arrays.fill(categorySelections, true);
            categories = new String[allCategories.size()];
            for (int i = 0; i < allCategories.size(); i++) {
                categories[i] = allCategories.get(i).getCategoryName();
            }
        });
        categoryDropdownThread.start();
    }

    private void populateTypeDropdown() {

        ReportType[] types = ReportType.values();
        List<String> typeStrings = new ArrayList<>();

        for (ReportType type : types) {
                typeStrings.add(type.toString());
                reportType = type;
            }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                Reports.this,
                R.layout.dropdown_item,
                typeStrings);

        typeDropdown.setAdapter(adapter);
    }

    @Override
    public void onDateSet(int month, int year) {
        String timeframe = null;
        calendar.set(Calendar.YEAR, year);
        if (Reports.reportType.equals(Reports.ReportType.MoM)) {
            calendar.set(Calendar.MONTH, month);
            timeframe = mdf.format(calendar.getTime());
        } else if (Reports.reportType.equals(ReportType.YoY)) {
            timeframe = ydf.format(calendar.getTime());
        }

        // Populate button picker labels
        if (selectedPicker == R.id.timeframeStartPicker) startPicker.setText(timeframe);
        else if (selectedPicker == R.id.timeframeEndPicker) endPicker.setText(timeframe);

        generateReport();
    }

    private void generateReport() {

        reportTable.removeAllViews();
        boolean categoryReady = false;
        boolean typeReady = false;
        boolean startReady = false;
        boolean endReady = false;
        Date startDate = null;
        Date endDate = null;
        List<Category> reportCategories = new ArrayList<>();

        // Category validation
        if (categorySelections != null) {
            for (int i = 0; i < categorySelections.length; i++) {
                if (categorySelections[i]) {
                    categoryReady = true;
                    reportCategories.add(allCategories.get(i));
                }
            }
        }

        // Type validation
        if (reportType != null) typeReady = true;

        // Start Timeframe validation
        String selectedStart = String.valueOf(startPicker.getText());
        if (!selectedStart.contains("Set") && typeReady) {
            if (reportType.equals(ReportType.MoM)) {
                try {
                    startDate = mdf.parse(selectedStart);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startReady = true;
            } else if (reportType.equals(ReportType.YoY)) {
                try {
                    startDate = ydf.parse(selectedStart);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startReady = true;
            }
        }

        // End Timeframe validation
        String selectedEnd = String.valueOf(endPicker.getText());
        if (!selectedEnd.contains("Set") && typeReady) {
            if (reportType.equals(ReportType.MoM)) {
                try {
                    endDate = mdf.parse(selectedEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                endReady = true;
            } else if (reportType.equals(ReportType.YoY)) {
                try {
                    endDate = ydf.parse(selectedEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                endReady = true;
            }
        }

        if (categoryReady && typeReady && startReady && endReady) {
            reportNotReady.setVisibility(View.GONE);
            reportTable.setVisibility(View.VISIBLE);

            Date finalStartDate = startDate;
            Date finalEndDate = endDate;

            // Add header rows
            addTableRow(-1,
                    getString(R.string.category),
                    startPicker.getText().toString(),
                    endPicker.getText().toString(),
                    getString(R.string.difference));
            rowNum = 0;

            Thread fetchTransactionsAndSortThread = new Thread(() -> {

                // Pull all transactions for user
                List<Transaction> userTransactions = repo.getTransactionsByUser(currentUserID);

                // Loop through categories and filter transactions
                for (Category category : reportCategories) {
                    long catID = category.getCategoryID();
                    double sumStart = 0;
                    double sumEnd = 0;
                    double difference;
                    rowNum++;

                    // Loop through category transactions to filter by date
                    for (Transaction transaction : userTransactions) {
                        Date transactionDate = null;
                        if (transaction.getCategory() == catID) {
                            String dateString = transaction.getDate();
                            StringBuilder newDateString = new StringBuilder();
                            if (reportType.equals(ReportType.MoM)) {
                                try {
                                    newDateString.append(dateString.substring(0,3));
                                    newDateString.append(dateString.substring(6,10));
                                    transactionDate = mdf.parse(newDateString.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (reportType.equals(ReportType.YoY)) {
                                try {
                                    newDateString.append(dateString.substring(6,10));
                                    transactionDate = ydf.parse(newDateString.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Add transaction amounts to totals
                            if (transactionDate != null) {
                                // Start bucket sums
                                if (transactionDate.equals(finalStartDate)) {
                                    sumStart += transaction.getAmount();
                                }
                                // End bucket sums
                                if (transactionDate.equals(finalEndDate)) {
                                    sumEnd += transaction.getAmount();
                                }
                            }
                        }
                    }
                    difference = sumEnd - sumStart;

                    // Populate the table row
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    addTableRow(rowNum, category.getCategoryName(),
                            nf.format(sumStart),
                            nf.format(sumEnd),
                            nf.format(difference));
                }
            });
            fetchTransactionsAndSortThread.start();
        } else {
            reportNotReady.setVisibility(View.VISIBLE);
            reportTable.setVisibility(View.GONE);
        }
    }

    private void addTableRow(int rowNum, String col1, String col2, String col3, String col4) {

        Reports.this.runOnUiThread(() -> {
            reportTable.setStretchAllColumns(true);
            TableRow row = new TableRow(Reports.this);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams cellParams = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(rowParams);

            // Column 1 data
            TextView column1 = new TextView(Reports.this);
            if (rowNum == -1) {
                column1.setAllCaps(true);
                column1.setTypeface(null, Typeface.BOLD);
                column1.setTextColor(getColor(R.color.white));
                column1.setBackgroundColor(getColor(R.color.dark_gray));
            } else {
                column1.setBackgroundColor(getColor(R.color.mid_gray));
            }
            column1.setText(col1);
            column1.setGravity(Gravity.CENTER);
            column1.setLayoutParams(cellParams);
            column1.setPadding(10,15,10,15);


            // Column 2 data
            TextView column2 = new TextView(Reports.this);
            if (rowNum == -1) {
                column2.setGravity(Gravity.CENTER);
                column2.setAllCaps(true);
                column2.setTypeface(null, Typeface.BOLD);
                column2.setTextColor(getColor(R.color.white));
                column2.setBackgroundColor(getColor(R.color.dark_gray));
            } else {
                column2.setGravity(Gravity.END);
            }
            column2.setText(col2);
            column2.setLayoutParams(cellParams);
            column2.setPadding(10,15,15,15);


            // Column 3 data
            TextView column3 = new TextView(Reports.this);
            if (rowNum == -1) {
                column3.setGravity(Gravity.CENTER);
                column3.setAllCaps(true);
                column3.setTypeface(null, Typeface.BOLD);
                column3.setTextColor(getColor(R.color.white));
                column3.setBackgroundColor(getColor(R.color.dark_gray));
            } else {
                column3.setGravity(Gravity.END);
                column3.setBackgroundColor(getColor(R.color.light_gray));
            }
            column3.setText(col3);
            column3.setLayoutParams(rowParams);
            column3.setPadding(10,15,15,15);

            // Column 4 data
            TextView column4 = new TextView(Reports.this);
            if (rowNum == -1) {
                column4.setGravity(Gravity.CENTER);
                column4.setAllCaps(true);
                column4.setTypeface(null, Typeface.BOLD);
                column4.setTextColor(getColor(R.color.white));
                column4.setBackgroundColor(getColor(R.color.dark_gray));
            } else {
                column4.setGravity(Gravity.END);
            }
            column4.setText(col4);
            column4.setLayoutParams(rowParams);
            column4.setPadding(10,15,15,15);

            row.addView(column1);
            row.addView(column2);
            row.addView(column3);
            row.addView(column4);

            reportTable.addView(row, new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        });
    }
}