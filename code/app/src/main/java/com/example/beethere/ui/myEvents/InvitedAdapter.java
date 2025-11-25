package com.example.beethere.ui.myEvents;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.UserListManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InvitedAdapter extends ArrayAdapter<User> {

    private Map<User, Boolean> invitedList;

    public InvitedAdapter(Context context, Map<User, Boolean> invitedList) {
        super(context, 0, new ArrayList<>(invitedList.keySet()));
        this.invitedList = invitedList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.content_invited, parent, false);
        } else {
            view = convertView;
        }

        User user = getItem(position);
        Boolean invited = invitedList.get(user);

        TextView name = view.findViewById(R.id.name);
        TextView inviteStatus = view.findViewById(R.id.inviteStatus);

        name.setText(user.getName());

        if (Boolean.TRUE.equals(invited)) {
            inviteStatus.setText("Declined");
            inviteStatus.setSelected(true);   // Only works if you defined a selector for "selected"
        } else {
            inviteStatus.setText("Awaiting");
            inviteStatus.setSelected(false);
        }

       // inviteStatus.setText();
//        String posterPath = user.getProfilePicture();
//        profilePicture.setImageURI(Uri.parse(posterPath));
        //TODO get profile picture set up
        return view;
    }


    public void update(Map<User, Boolean> newList) {
        invitedList.clear();
        invitedList.putAll(newList);

        clear();
        addAll(new ArrayList<>(newList.keySet()));
        notifyDataSetChanged();
    }


}
