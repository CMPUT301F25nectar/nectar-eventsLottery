package com.example.beethere.ui.notifications;


import com.example.beethere.DeviceId;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.notifications_classes.Notification;
import com.example.beethere.notifications_classes.NotificationHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private ListView notificationsList;
    private NotificationsAdapter adapter;
    private ArrayList<Notification> notificationItems;
    private NotificationHandler notificationHandler;
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
        notificationHandler = new NotificationHandler();

        loadUserNotifications();
//        notificationsList.setOnItemClickListener((parent, view1, position, id) -> {
//            Toast.makeText(requireContext(), "Clicked notification" + position, Toast.LENGTH_SHORT).show();
//        });

        return view;
    }

    private void loadUserNotifications(){
        String deviceId = DeviceId.get(requireContext());

        notificationHandler.setupNotificationListener(deviceId, new NotificationHandler.NotificationCallback() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                notificationItems.clear();
                notificationItems.addAll(notifications);

//                for (Notification notif : notifications){
//                    String text = notif.getEventName() + "\n" + notif.getMessage() + "\n" + getTimeAgo(notif.getTimestamp());
//                    notificationItems.add(text);
//                }

                adapter.notifyDataSetChanged();

                if(notifications.isEmpty()){
                    Toast.makeText(requireContext(), "No notifications yet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String getDeviceid(){
        return DeviceId.get(requireContext());
    }



}