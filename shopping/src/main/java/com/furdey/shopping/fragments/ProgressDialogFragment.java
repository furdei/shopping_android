package com.furdey.shopping.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.furdey.shopping.tasks.RebuildIndicesTask;

/**
 * Created by Masya on 07.01.2015.
 */
public class ProgressDialogFragment extends DialogFragment implements RebuildIndicesTask.ProgressListener
{

    public static ProgressDialogFragment createInstance(String title, String message, int max) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle arguments = new Bundle(2);
        arguments.putString(TITLE_PARAM, title);
        arguments.putString(MESSAGE_PARAM, message);
        arguments.putInt(MAX_PARAM, max);
        fragment.setArguments(arguments);
        return fragment;
    }

    private static final String TITLE_PARAM = "title";
    private static final String MESSAGE_PARAM = "message";
    private static final String MAX_PARAM = "max";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setTitle(getArguments().getString(TITLE_PARAM));
        dialog.setMessage(getArguments().getString(MESSAGE_PARAM));
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(getArguments().getInt(MAX_PARAM));
        return dialog;
    }

    @Override
    public void updateProgress(int value) {
        ProgressDialog dialog = (ProgressDialog) getDialog();
        dialog.setProgress(value);
    }
}
