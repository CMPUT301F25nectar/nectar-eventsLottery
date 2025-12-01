package com.example.beethere.ui.adminDashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.beethere.R;
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

        titleText.setText(notif.getEventName() != null ? notif.getEventName() : "Admin Message");
        messageText.setText(notif.getMessage());
        Date date = new Date(notif.getTimestamp());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        timeStampText.setText(sdf.format(date));

        return convertView;
    }

}
