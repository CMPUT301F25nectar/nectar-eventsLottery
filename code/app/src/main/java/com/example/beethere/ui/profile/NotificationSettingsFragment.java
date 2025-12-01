package com.example.beethere.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.device.DeviceId;

public class NotificationSettingsFragment extends Fragment {
    private SwitchCompat winningSwitch;
    private SwitchCompat losingSwitch;
    private SwitchCompat cancelledSwitch;
    //private SwitchCompact organizerSwitch;
    private SwitchCompat adminSwitch;

    private DatabaseFunctions dbFunctions;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_notifications_settings, container, false);
        deviceId = DeviceId.get(requireContext());
        dbFunctions = new DatabaseFunctions();
        winningSwitch = view.findViewById(R.id.winning_switch);
        losingSwitch = view.findViewById(R.id.losing_switch);
        cancelledSwitch = view.findViewById(R.id.cancelled_switch);
        adminSwitch = view.findViewById(R.id.admin_switch);

        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(NotificationSettingsFragment.this).navigateUp()
        );

        loadNotificationPreferences();
        setupSwitchListeners();
        return view;
    }
    private void loadNotificationPreferences(){
        dbFunctions.getUserDB(deviceId, new DatabaseCallback<User>(){
            @Override
            public void onCallback(User user){
                if(user != null){
                    winningSwitch.setChecked(user.getReceiveWinningNotifs());
                    losingSwitch.setChecked(user.getReceiveLosingNotifs());
                    cancelledSwitch.setChecked(user.getReceiveCancelledNotifs());
                    adminSwitch.setChecked(user.getReceiveAdminNotifs());
                }
            }
            @Override
            public void onError(Exception e) {
                Log.e("NotificationSettings", "Error loading preferences", e);
                Toast.makeText(getContext(), "Failed to load preferences", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setupSwitchListeners(){
        winningSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                dbFunctions.updateNotificationPreference(deviceId, "receiveWinningNotifs", isChecked)
        );
        losingSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                dbFunctions.updateNotificationPreference(deviceId, "receiveLosingNotifs", isChecked)
        );
        cancelledSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                dbFunctions.updateNotificationPreference(deviceId, "receiveCancelledNotifs", isChecked)
        );
        adminSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                dbFunctions.updateNotificationPreference(deviceId, "receiveAdminNotifs", isChecked)
        );
    }
}
