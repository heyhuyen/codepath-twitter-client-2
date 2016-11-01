package com.huyentran.tweets.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.huyentran.tweets.R;

/**
 * {@link AlertDialog} to prompt users to save draft of tweet.
 */
public class SaveDraftDialogFragment extends DialogFragment {

    private DraftDialogListener listener;

    public interface DraftDialogListener {
        void onSave();
        void onDelete();
    }

    public SaveDraftDialogFragment() {
    }

    public static SaveDraftDialogFragment newInstance() {
        SaveDraftDialogFragment fragment = new SaveDraftDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.listener = (DraftDialogListener) getTargetFragment();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.save_draft)
                .setPositiveButton(R.string.save, (dialog, id) -> listener.onSave())
                .setNegativeButton(R.string.delete, (dialog, id) -> listener.onDelete());
        return builder.create();
    }
}
