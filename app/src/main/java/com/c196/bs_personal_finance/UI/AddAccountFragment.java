package com.c196.bs_personal_finance.UI;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import com.c196.bs_personal_finance.R;

public class AddAccountFragment extends DialogFragment {

    private Button cancel;
    private Button save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_add_account, container, false);



    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AppCompatDialog(getActivity(), R.style.AlertDialog);
    }
}
