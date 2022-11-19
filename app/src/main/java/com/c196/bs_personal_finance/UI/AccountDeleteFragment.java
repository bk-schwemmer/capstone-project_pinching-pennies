package com.c196.bs_personal_finance.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class AccountDeleteFragment extends DialogFragment {


    public interface OnAccountDeletedListener {
        void onDeleteOverride();
    }

    private OnAccountDeletedListener mListener;


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle("Delete Account")
                .setMessage("The account can't be deleted because there are transactions assigned to it." +
                        "Click override to delete all associated transactions and the account.")
                .setPositiveButton("DELETE", (dialogInterface, whichButton) -> mListener.onDeleteOverride())
                .setNegativeButton("Cancel", null)
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnAccountDeletedListener) context;
    }
}
