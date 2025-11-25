package com.example.beethere.ui.myEvents;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.beethere.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConfirmDeleteFragment extends DialogFragment {

    private String eventID;
    public interface OnEventDeletedListener {
        void onEventDeleted(String eventID);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (300 * getResources().getDisplayMetrics().density);
            int height = (int) (300 * getResources().getDisplayMetrics().density);

            getDialog().getWindow().setLayout(width, height);
        }
    }


    private OnEventDeletedListener listener;

    public void setOnEventDeletedListener(OnEventDeletedListener listener) {
        this.listener = listener;
    }

    public ConfirmDeleteFragment(String eventID) {
        this.eventID = eventID;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_delete_event,null);
        builder.setView(view);

        Button cancel = view.findViewById(R.id.cancelButton);
        Button delete = view.findViewById(R.id.deleteButton);

        cancel.setOnClickListener(v -> dismiss());

        delete.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(eventID)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onEventDeleted(eventID);
                        }
                        dismiss();
                    })
                    .addOnFailureListener(e -> e.printStackTrace());
        });

        return builder.create();
    }
}
