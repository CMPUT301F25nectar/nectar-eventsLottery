package com.example.beethere.ui.adminDashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.adapters.AdminNotificationAdapter;
import com.example.beethere.notifications_classes.Notification;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationsFragment extends Fragment {

    private ListView notificationLogsList;
    private ImageButton backButton;
    private AdminNotificationAdapter adapter;
    private ArrayList<Notification> notifications;
    private DatabaseFunctions dbFunctions;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_admin_view_notification, container, false);
        notificationLogsList = view.findViewById(R.id.admin_notifications_list);
        backButton = view.findViewById(R.id.backButton);
        notifications = new ArrayList<>();
        dbFunctions = new DatabaseFunctions();

        adapter = new AdminNotificationAdapter(requireContext(), notifications);
        notificationLogsList.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        loadNotifications();

        return view;
    }

    private void loadNotifications(){
        dbFunctions.getAllNotifications(new DatabaseCallback<>() {
            @Override
            public void onCallback(List<Notification> result) {
                notifications.clear();
                notifications.addAll(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e("adminNotification", "error loading notifications", e);
            }
        });
    }

}
