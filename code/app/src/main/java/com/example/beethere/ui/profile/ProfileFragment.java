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

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.device.DeviceId;
import com.example.beethere.R;
import com.example.beethere.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * In app profile screen to view/edit/delete the current device's profile
 */
public class ProfileFragment extends Fragment {
    private EditText firstname, lastname, emailid, phone;
    private TextView userdeviceId;
    DatabaseFunctions dbFunctions = new DatabaseFunctions();

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
                                .addOnSuccessListener((DocumentSnapshot snap)->{
                                    User u = snap.toObject(User.class);//user class
                                    if (u==null) return;
                                    String fullname = u.getName();
                                    if (fullname!=null){
                                        String[] split = fullname.split(" ",2);
                                        firstname.setText(split[0]);
                                        if (split.length>1) lastname.setText(split[1]);
                                    }
                                    emailid.setText(u.getEmail());
                                    phone.setText(u.getPhone());
                                }
                                );
    }
    private void saveprofile() {
        String deviceID = DeviceId.get(requireContext());
        String first = firstname.getText().toString();
        String last = lastname.getText().toString();
        String email = emailid.getText().toString();
        String phonenumber = phone.getText().toString();
        if (first.isEmpty() && last.isEmpty()){
            Toast.makeText(requireContext(), "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()){
            Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        String fullname = (first) + " " + (last);

        User u = new User();
        u.setName(fullname);
        u.setEmail(email);
        u.setPhone(phonenumber);
        u.setDeviceid(deviceID);
        //issue here
        u.setAdmin(false);
        u.setOrganizer(true);
        dbFunctions.addUserDB(u);
    }
    private void deleteprofile() {
        String deviceID = DeviceId.get(requireContext());
        dbFunctions.deleteUserDB(deviceID);
    }

}