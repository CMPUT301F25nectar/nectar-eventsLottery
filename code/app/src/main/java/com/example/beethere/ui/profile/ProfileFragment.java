package com.example.beethere.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.beethere.DeviceId;
import com.example.beethere.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {
    private EditText firstname, lastname, emailid, phone;
    private TextView userdeviceId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firstname = view.findViewById(R.id.first_name);
        lastname = view.findViewById(R.id.last_name);
        emailid = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        userdeviceId = view.findViewById(R.id.DeviceId);

        Button btnsave = view.findViewById(R.id.savebtn);
        Button btndelete = view.findViewById(R.id.deletebtn);

        String deviceID = DeviceId.get(requireContext());
        userdeviceId.setText("User device id: " + deviceID);
        profile();
        btnsave.setOnClickListener(v -> saveprofile());
        btndelete.setOnClickListener(v -> deleteprofile());
        return view;
    }

    private void profile(){
        String deviceID = DeviceId.get(requireContext());
        FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(deviceID)
                        .get()
                                .addOnSuccessListener((DocumentSnapshot snapshot)->{
                                    if (snapshot.exists()){
                                        String fullname = snapshot.getString("name");
                                        if (fullname!=null){
                                            String[] split = fullname.split(" ",2);
                                            firstname.setText(split[0]);
                                            if (split.length>1) lastname.setText(split[1]);
                                        }
                                    }
                                    emailid.setText(snapshot.getString("email"));
                                    phone.setText(snapshot.getString("phone"));
                                });
    }
    private void saveprofile() {
        String deviceID = DeviceId.get(requireContext());
        String fullname = firstname.getText().toString() + " " + lastname.getText().toString();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID)
                .set(new ProfileDialogFragment.Simple(
                        fullname, emailid.getText().toString(), phone.getText().toString()
                ))
                .addOnSuccessListener(unused -> Toast.makeText(requireContext(), "Updated profile!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(fail -> Toast.makeText(requireContext(), "Failed updating profile"+ fail.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void deleteprofile() {
        String deviceID = DeviceId.get(requireContext());
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Deleted Profile", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(fail -> Toast.makeText(requireContext(), "Failed deleting profile", Toast.LENGTH_SHORT).show()
                );
    }

}