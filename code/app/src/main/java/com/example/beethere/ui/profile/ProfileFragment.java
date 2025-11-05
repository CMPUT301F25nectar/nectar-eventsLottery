package com.example.beethere.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.beethere.R;
import com.example.beethere.Session;
import com.example.beethere.User;


public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView DeviceId =  view.findViewById(R.id.DeviceId);
        String androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId==null) androidId="unknown";
        DeviceId.setText("Device Id: "+androidId);

        EditText firstname = view.findViewById(R.id.first_name);
        EditText lastname = view.findViewById(R.id.last_name);
        EditText emailid = view.findViewById(R.id.email);
        EditText phone = view.findViewById(R.id.phone);
        Button btnsave = view.findViewById(R.id.savebtn);
        Button btndelete = view.findViewById(R.id.deletebtn);

        String finalAndroidId = androidId;
        btnsave.setOnClickListener(view1-> {
                    String name = (firstname.getText().toString() + " " + lastname.getText().toString());
                    String email = emailid.getText().toString();
                    String phonenumber = phone.getText().toString();
                    if (name.isEmpty() || email.isEmpty()) {
                        android.widget.Toast.makeText(requireContext(), "Enter name and email id", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User user = new User(name, email);
                    user.setPhone(phonenumber);
                    user.setDeviceid(finalAndroidId);
                    user.setAdmin(false);

                    Session.save(user);//local memory for now- to be replaced by firebase
                    Toast.makeText(requireContext(), "Profile information saved", Toast.LENGTH_SHORT).show();
                });
        btndelete.setOnClickListener(view1->{
                Session.clear();//to be replaced
                firstname.setText("");
                lastname.setText("");
                emailid.setText("");
                phone.setText("");
                Toast.makeText(requireContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
            });
        return view;
    }
}