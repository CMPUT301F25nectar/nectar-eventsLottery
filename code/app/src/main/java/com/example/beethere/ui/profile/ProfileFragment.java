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
import androidx.navigation.fragment.NavHostFragment;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.device.DeviceId;
import com.example.beethere.R;
import com.example.beethere.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.beethere.ui.profile.NotificationSettingsFragment;

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

        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        emailid = view.findViewById(R.id.edit_email);
        phone = view.findViewById(R.id.edit_phone);

        Button btnsave = view.findViewById(R.id.button_save_profile);
        TextView personalSettings = view.findViewById(R.id.personal_settings);
        TextView notificationsSettings = view.findViewById(R.id.notification_settings);
        profile();
        btnsave.setOnClickListener(v -> saveprofile());
        //go to personal settings screen
        personalSettings.setOnClickListener(v ->
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.personalSettingsFragment)
        );
        //notification settingss TO DO
        notificationsSettings.setOnClickListener(v ->
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.notificationSettingsFragment)
        );

        return view;
    }
//gets profile information for a device
    private void profile(){
        String deviceID = DeviceId.get(requireContext());
        FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(deviceID)
                        .get()
                                .addOnSuccessListener((DocumentSnapshot snap)->{
                                    if(!snap.exists()){
                                        clear();//clear fields
                                        return;
                                    }
                                    //return nothing, if the profile doesnt exist
                                    User u = snap.toObject(User.class);//user class
                                    if (u==null) {
                                        clear();
                                        return;
                                    }
                                    //split full name into first and last
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

    /**
     * saving profile to firestore
     */
    private void saveprofile() {
        String deviceID = DeviceId.get(requireContext());
        String first = firstname.getText().toString();
        String last = lastname.getText().toString();
        String email = emailid.getText().toString();
        String phonenumber = phone.getText().toString();
        //name is a mandatory field
        if (first.isEmpty() && last.isEmpty()){
            Toast.makeText(requireContext(), "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        //email is a mandatory field
        if (email.isEmpty()){
            Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        String fullname = (first) + " " + (last);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID)
                .get()
                .addOnSuccessListener(snap->{
                    Boolean admincurrent = null;
                    Boolean organizercurrent = null;
                    Boolean violationcurrent = null;
                    if(snap.exists()){
                        User exists = snap.toObject(User.class);
                        if (exists!= null) {
                            admincurrent = exists.getAdmin();
                            organizercurrent=exists.getOrganizer();
                            violationcurrent=exists.getViolation();
                        }
                    }
                    User u = new User();
                    u.setName(fullname);
                    u.setEmail(email);
                    u.setPhone(phonenumber);
                    u.setDeviceid(deviceID);

                    //set admin and organizer default flags
                    if (violationcurrent!=null) u.setViolation(violationcurrent);
                    else u.setViolation(Boolean.FALSE);
                    if (admincurrent!=null) u.setAdmin(admincurrent);
                    else u.setAdmin(Boolean.FALSE);
                    if (organizercurrent!=null) u.setOrganizer(organizercurrent);
                    else u.setOrganizer(Boolean.FALSE);
                    FirebaseFirestore.getInstance().collection("users")
                            .document(deviceID)
                            .set(u)
                            .addOnSuccessListener(unused->
                                Toast.makeText(requireContext(), "Saved updates!", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(fail-> Toast.makeText(requireContext(), "failed to save",Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(fail -> Toast.makeText(requireContext(), "Failed updating profile"+ fail.getMessage(), Toast.LENGTH_SHORT).show());
    }
    //deleting the device's profile and clearing the fields
    private void deleteprofile() {
        String deviceID = DeviceId.get(requireContext());
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID)
                .delete()
                .addOnSuccessListener(unused -> {
                    clear();
                    Toast.makeText(requireContext(), "Deleted Profile", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(fail -> Toast.makeText(requireContext(), "Failed deleting profile", Toast.LENGTH_SHORT).show()
                );
    }

    private void clear() {
        firstname.setText("");
        lastname.setText("");
        emailid.setText("");
        phone.setText("");
    }

}