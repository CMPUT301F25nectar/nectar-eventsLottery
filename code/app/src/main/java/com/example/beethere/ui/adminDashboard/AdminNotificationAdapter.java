package com.example.beethere.ui.adminDashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.notifications_classes.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminNotificationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Notification> notifications;

    public AdminNotificationAdapter(Context context, ArrayList<Notification> notifications){
        this.context = context;
        this.notifications = notifications;
    }
    @Override
    public int getCount(){
        return notifications.size();
    }
    @Override
    public Object getItem(int position){
        return notifications.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_item_admin_notification_log, parent, false);
        }
        Notification notif = notifications.get(position);
        TextView titleText = convertView.findViewById(R.id.user_name);
        TextView messageText = convertView.findViewById(R.id.message);
        TextView timeStampText = convertView.findViewById(R.id.timestamp);

        if (notif.getDeviceIds() != null && !notif.getDeviceIds().isEmpty()) {
            String deviceId = notif.getDeviceIds().get(0);
            loadUserName(deviceId, titleText);
        } else {
            titleText.setText("Unknown User");
        }


        //titleText.setText(notif.getEventName() != null ? notif.getEventName() : "Admin Message");
        messageText.setText(notif.getMessage());
        timeStampText.setText(getTimeAgo(notif.getTimestamp()));

        return convertView;
    }
    private void loadUserName(String deviceId, TextView titleText) {
        DatabaseFunctions db = new DatabaseFunctions();

        db.getUserDB(deviceId, new DatabaseCallback<User>() {
            @Override
            public void onCallback(User user) {
                if (user != null) {
                    titleText.setText(user.getName());
                } else {
                    titleText.setText("Unknown User");
                }
            }

            @Override
            public void onError(Exception e) {
                titleText.setText("Unknown User");
            }
        });
    }
    private String getTimeAgo(long timestamp){
        long difference = System.currentTimeMillis() - timestamp;
        long seconds = difference/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        long days = hours/24;

        if (days > 0){
            return days + "d ago";
        } else if (hours > 0) {
            return hours + "h ago";
        } else if (minutes > 0) {
            return minutes + "m ago";
        } else {
            return "Just now";
        }
    }




}
