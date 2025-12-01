package com.example.beethere.ui.adminDashboard;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.ui.myEvents.ConfirmDeleteFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class ConfirmDeleteProfileFragment extends DialogFragment {

    private User user;

    public interface OnProfileDeletedListener {
        void onProfileDeleted(User user);
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
    public ConfirmDeleteProfileFragment(User user) {
        this.user = user;
    }

    private ConfirmDeleteProfileFragment.OnProfileDeletedListener listener;

    public void setOnProfileDeletedListener(ConfirmDeleteProfileFragment.OnProfileDeletedListener listener) {
        this.listener = listener;
    }

    /**
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return view
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_delete_event,null);
        builder.setView(view);

        Button cancel = view.findViewById(R.id.cancelButton);
        Button delete = view.findViewById(R.id.deleteButton);
        TextView areYouSure = view.findViewById(R.id.areYouSure);

        areYouSure.setText("Are you sure you'd like to delete this profile?");

        cancel.setOnClickListener(v -> dismiss());

        delete.setOnClickListener(v -> {
            DatabaseFunctions dbFunctions = new DatabaseFunctions();
            dbFunctions.deleteUserDB(user);

            if (listener != null) {
                listener.onProfileDeleted(user);
            }

            dismiss();
        });


        return builder.create();
    }
}
