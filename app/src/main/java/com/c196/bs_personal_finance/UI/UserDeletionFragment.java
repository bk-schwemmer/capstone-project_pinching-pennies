package com.c196.bs_personal_finance.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.c196.bs_personal_finance.R;

public class UserDeletionFragment extends DialogFragment {


    public interface OnUserDeletedListener {
        void onDeleteOverride();
    }

    private UserDeletionFragment.OnUserDeletedListener mListener;

    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle("Delete User")
                .setMessage("Deleting a user will remove all accounts and transactions associated with it.\n" +
                        "Are you sure you want to proceed?")
                .setPositiveButton(getString(R.string.delete_user), (dialogInterface, whichButton) -> mListener.onDeleteOverride())
                .setNegativeButton(getString(R.string.cancel), null)
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (UserDeletionFragment.OnUserDeletedListener) context;
    }
}
