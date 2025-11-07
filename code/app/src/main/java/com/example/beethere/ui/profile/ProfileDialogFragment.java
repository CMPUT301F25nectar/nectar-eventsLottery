package com.example.beethere.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.beethere.DeviceId;
import com.example.beethere.R;
import com.example.beethere.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileDialogFragment extends DialogFragment {
    public static Runnable saved;
    public static void show(androidx.fragment.app.FragmentManager fragmentmanager, Runnable cb){
        saved =cb;
        new ProfileDialogFragment().show(fragmentmanager, "New Profile Dialog");
    }
    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_createprofile,null);

        EditText EnterName = view.findViewById(R.id.entername);
        EditText EnterEmail = view.findViewById(R.id.enteremail);
        EditText EnterPhone = view.findViewById(R.id.enterphone);

        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        builder.setTitle("Create new profile");
        builder.setView(view);
        builder.setPositiveButton("Create account", ((dialog, which) -> {
                    String name = EnterName.getText().toString();
                    String email = EnterEmail.getText().toString();
                    String phone = EnterPhone.getText().toString();
                    String deviceId = DeviceId.get(requireContext());
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(requireContext(), "Enter your name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(requireContext(), "Enter your email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User u = new User();
                    u.setName(name);
                    u.setEmail(email);
                    u.setPhone(phone);
                    u.setDeviceid(deviceId);
                    u.setAdmin(false);

                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(deviceId)
                            .set(u)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(requireContext(), "Account created!", Toast.LENGTH_SHORT).show();
                                if (saved != null) saved.run();
                            })
                            .addOnFailureListener(fail ->
                                    Toast.makeText(requireContext(), "Failed: " + fail.getMessage(), Toast.LENGTH_LONG).show()
                            );
                }));
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

}

