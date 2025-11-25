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
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
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

        /* TODO
         * write code here
         */
        notificationsList = view.findViewById(R.id.notifications_list);
        notificationItems = new ArrayList<>();

        adapter = new NotificationsAdapter(notificationItems, eventId -> {
            Toast.makeText(requireContext(), "Would navigate to event: " + eventId, Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            Navigation.findNavController(view).navigate(R.id.notificationsToEventDetails, bundle);

        });
        notificationsList.setAdapter(adapter);
        currentUserDeviceid = getDeviceid();
        dbfunctions = new DatabaseFunctions();

        loadUserNotifications();
//        notificationsList.setOnItemClickListener((parent, view1, position, id) -> {
//            Toast.makeText(requireContext(), "Clicked notification" + position, Toast.LENGTH_SHORT).show();
//        });

        return view;
    }

    private void loadUserNotifications(){
        String deviceId = DeviceId.get(requireContext());

        DatabaseCallback<List<Notification>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(List<Notification> result) {
                notificationItems.clear();
                notificationItems.addAll(result);
                //                for (Notification notif : notifications){
//                    String text = notif.getEventName() + "\n" + notif.getMessage() + "\n" + getTimeAgo(notif.getTimestamp());
//                    notificationItems.add(text);
//                }
                adapter.notifyDataSetChanged();

                if(result.isEmpty()){
                    Toast.makeText(requireContext(), "No notifications yet!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(Exception e) {
                Log.d("AllEvents", "Notifications fragment error getting notifications from database");
            }
        };

        //dbfunctions.getNotifsDB(deviceId, callback);
    }



    private String getDeviceid(){
        return DeviceId.get(requireContext());
    }



}