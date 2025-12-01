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

public class GoBackDialogFragment extends DialogFragment {

    private Button cancel;
    private Button confirm;

    public interface GoBackListener {
        void onConfirmGoBack();
    }

    private GoBackListener listener;

    public void setGoBackListener(GoBackListener listener) {
        this.listener = listener;
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

    /**
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return return generated dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_go_back, null);
        builder.setView(view);

        cancel = view.findViewById(R.id.cancelButton);
        confirm = view.findViewById(R.id.confirmButton);

        cancel.setOnClickListener(v -> dismiss());

        confirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmGoBack();
            }
            dismiss();
        });

        return builder.create();
    }
}
