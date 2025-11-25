package com.example.beethere.ui.profile;
/**
 * Dialog to create a new profile for a user (name, email, phone, admin and boolean flags) for each deviceid.
 * Writes to the users collection with admin set as false and organizer set as true by default
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.device.DeviceId;
import com.example.beethere.R;
import com.example.beethere.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileDialogFragment extends DialogFragment {
    public static Runnable saved;
    private DatabaseFunctions dbfunctions;

    public static void show(androidx.fragment.app.FragmentManager fragmentmanager, Runnable cb){
        saved =cb;
        new ProfileDialogFragment().show(fragmentmanager, "New Profile Dialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState){
        dbfunctions = new DatabaseFunctions();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_createprofile,null);

        EditText EnterFirstName = view.findViewById(R.id.enterfirstname);
        EditText EnterLastName = view.findViewById(R.id.enterlastname);
        EditText EnterEmail = view.findViewById(R.id.enteremail);
        EditText EnterPhone = view.findViewById(R.id.enterphone);
        Button signupButton = view.findViewById(R.id.signup_button);

        AlertDialog dialog =new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(true)
                .create();
        signupButton.setOnClickListener(v->{
            String first = EnterFirstName.getText().toString();
            String last = EnterLastName.getText().toString();
            String email = EnterEmail.getText().toString();
            String phone = EnterPhone.getText().toString();
            String deviceId = DeviceId.get(requireContext());
            String name = (first+ " "+last);
            if (name.isEmpty() || email.isEmpty()){
                Toast.makeText(requireContext(), "Please enter name and email", Toast.LENGTH_SHORT).show();
                return;
            }
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPhone(phone);
            u.setDeviceid(deviceId);
            u.setAdmin(false);
            u.setOrganizer(true);
            u.setViolation(false);

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(deviceId)
                    .set(u)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(requireContext(), "Account created!", Toast.LENGTH_SHORT).show();
                        if (saved != null) saved.run();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(fail ->
                            Toast.makeText(requireContext(), "Failed: " + fail.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
       return dialog;
    }

}

