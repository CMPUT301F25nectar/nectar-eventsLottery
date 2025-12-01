package com.example.beethere.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.beethere.R;
import com.example.beethere.notifications_classes.Notification;

import java.util.ArrayList;

public class NotificationsAdapter extends BaseAdapter {
    private ArrayList<Notification> notifications;
    private OnGoToEventClickListener listener;

    public interface OnGoToEventClickListener{
        void onGoToEventClick(String eventId);
    }

    public NotificationsAdapter(ArrayList<Notification> notifications, OnGoToEventClickListener listener){
        this.notifications = notifications;
        this.listener = listener;
    }

    @Override
    public int getCount(){
        return notifications.size();
    }
    @Override
    public Object getItem(int position){
        return notifications.get(position);
    }
    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        }
        Notification notification = notifications.get(position);
        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView message = convertView.findViewById(R.id.message);
        TextView timestamp = convertView.findViewById(R.id.timestamp);
        Button btnGoToEvent = convertView.findViewById(R.id.btn_go_to_event);

        eventName.setText(notification.getEventName());
        message.setText(notification.getMessage());
        timestamp.setText(getTimeAgo(notification.getTimestamp()));
        btnGoToEvent.setOnClickListener(v -> {
            if(listener!=null){
                listener.onGoToEventClick(notification.getEventId());
            }
        });
        return convertView;
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
