package com.example.beethere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.beethere.eventclasses.Event;

public class RemovalConfirmation extends DialogFragment {

    interface RemovalConfirmationDialogListener{
        void removalConfirmation(Event event);
    };


    private String title;
    private String positive;
    private String description;
    private Event event;
    private RemovalConfirmationDialogListener removalConfirmationListener;

    void setTitle(String title) {
        this.title = title;
    }

    void setPositive(String positive) {
        this.positive = positive;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setEvent(){
        this.event = event;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RemovalConfirmationDialogListener) {
            removalConfirmationListener = (RemovalConfirmationDialogListener) context;
        }
        else {
            throw new RuntimeException(context +
                    "must implement RemovalConfirmationDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_removal_confirmation, null);
        TextView textView = view.findViewById(R.id.removal_description);
        textView.setText(description);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle(this.title)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(this.positive, ((dialog, which) -> {
                    // TODO
                    // figure out how to send function??
                    removalConfirmationListener.removalConfirmation(this.event);
                }))
                .create();
    }

}
