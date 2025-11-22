package com.example.beethere.ui.myEvents;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;

public class WaitListandRegisteredAdapter extends ArrayAdapter<User>{

    private ArrayList<User> users;

    public WaitListandRegisteredAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.content_waitlist_and_registered, parent, false);
        } else {
            view = convertView;
        }

        User user = users.get(position);
        TextView name =  view.findViewById(R.id.name);
        name.setText(user.getName());
        return view;
    }

    public void update(ArrayList<User> newList) {
        clear();
        addAll(newList);
        notifyDataSetChanged();
    }

}
