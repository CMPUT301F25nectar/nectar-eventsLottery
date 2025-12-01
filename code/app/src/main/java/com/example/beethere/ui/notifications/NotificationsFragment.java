package com.example.beethere.ui.notifications;


import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.device.DeviceId;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.example.beethere.notifications_classes.Notification;
import com.example.beethere.notifications_classes.NotificationHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private ListView notificationsList;
    private NotificationsAdapter adapter;
    private ArrayList<Notification> notificationItems;
    private DatabaseFunctions dbfunctions;
    private String currentUserDeviceid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationsList = view.findViewById(R.id.notifications_list);
        notificationItems = new ArrayList<>();

        adapter = new NotificationsAdapter(notificationItems, eventId -> {
            Toast.makeText(requireContext(), "Loading event...", Toast.LENGTH_SHORT).show();

            // Use DatabaseFunctions to load the event
            dbfunctions.getEvent(eventId, new DatabaseCallback<Event>() {
                @Override
                public void onCallback(Event event) {
                    if (event != null) {
                        // Set event in ViewModel
                        EventDataViewModel eventData = new ViewModelProvider(requireActivity()).get(EventDataViewModel.class);
                        eventData.setEvent(event);

                        // Now navigate
                        Navigation.findNavController(requireView()).navigate(R.id.notificationsToEventDetails);

                        Log.d("NotifTab", "Event loaded and navigation successful!");
                    } else {
                        Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("NotifTab", "Error loading event", e);
                    Toast.makeText(requireContext(), "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        notificationsList.setAdapter(adapter);
        currentUserDeviceid = getDeviceid();
        dbfunctions = new DatabaseFunctions();

        loadUserNotifications();

        return view;
    }

    private void loadUserNotifications(){
        String deviceId = DeviceId.get(requireContext());

        Toast.makeText(requireContext(), "Device ID: " + deviceId, Toast.LENGTH_LONG).show();

        Log.d("NotifTab", "Loading notifications ");
        Log.d("NotifTab", "Device ID: " + deviceId);

        DatabaseCallback<List<Notification>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(List<Notification> result) {
                Log.d("NotifTab", "SUCCESS: Loaded " + result.size() + " notifications");

                for (Notification n : result) {
                    Log.d("NotifTab", "  - " + n.getType() + ": " + n.getMessage());
                }

                notificationItems.clear();
                notificationItems.addAll(result);
                adapter.notifyDataSetChanged();

                if(result.isEmpty()){
                    Toast.makeText(requireContext(), "No notifications yet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("NotifTab", "ERROR loading notifications", e);
                Toast.makeText(requireContext(), "Error loading notifications", Toast.LENGTH_SHORT).show();
            }
        };

        dbfunctions.getNotifsDB(deviceId, callback);  // ← UNCOMMENTED!
    }



    private String getDeviceid(){
        return DeviceId.get(requireContext());
    }



}