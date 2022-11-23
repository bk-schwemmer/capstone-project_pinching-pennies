package com.c196.bs_personal_finance.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.c196.bs_personal_finance.R;

public class IncompleteEntryFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.incomplete)
                .setMessage(R.string.incomplete_message)
                .setPositiveButton(R.string.ok, null)
                .create();
    }
}