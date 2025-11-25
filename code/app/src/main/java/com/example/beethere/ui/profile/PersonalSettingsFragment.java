package com.example.beethere.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beethere.R;
import com.example.beethere.device.DeviceId;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonalSettingsFragment extends Fragment {
    private TextView deviceIdText;
    private Button deleteAccountButton;
    private FirebaseFirestore db;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_settings, container, false);
        deviceIdText = view.findViewById(R.id.device_id_value);
        deleteAccountButton = view.findViewById(R.id.button_delete_account);

        db = FirebaseFirestore.getInstance();
        deviceId = DeviceId.get(requireContext());
        deviceIdText.setText(deviceId);
        deleteAccountButton.setOnClickListener(v -> deleteconfirmation());
        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v-> NavHostFragment.findNavController(PersonalSettingsFragment.this).popBackStack());
        return view;
    }

    private void deleteconfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete account")
                .setMessage("Are you sure you want to delete your account?")
                .setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Delete", (dialog, which) -> {
                    deleteAccountFromFirestore();
                }).show();

    }

    private void deleteAccountFromFirestore() {
        db.collection("users")
                .document(deviceId)
                .delete()
                .addOnSuccessListener(unused -> {
                    deleteddialog();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(),
                            "Failed to delete account: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void deleteddialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_account_deleted, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();
        Button closeButton = dialogView.findViewById(R.id.button_close);
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            ProfileDialogFragment.show(getParentFragmentManager(), (Runnable) null);
        });
        dialog.show();
    }
}