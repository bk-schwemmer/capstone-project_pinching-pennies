package com.c196.bs_personal_finance.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;

import com.c196.bs_personal_finance.R;

import java.time.Month;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface ReturnTimeframeListener {
        void onDateSet(int month, int year);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogTheme);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                R.style.dialogTheme,
                this,
                year,
                month,
                1);

        LinearLayout pickerParentLayout = (LinearLayout) dialog.getDatePicker().getChildAt(0);
        LinearLayout pickerSpinnersHolder = (LinearLayout) pickerParentLayout.getChildAt(0);
        pickerSpinnersHolder.getChildAt(1).setVisibility(View.GONE);

        if (Reports.reportType.equals(Reports.ReportType.YoY)) {
            pickerSpinnersHolder.getChildAt(0).setVisibility(View.GONE);
        }

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        ReturnTimeframeListener listener = (ReturnTimeframeListener) getActivity();
        listener.onDateSet(month, year);
    }
}
