package com.example.beethere.eventclasses.eventDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;

public class InviteButtons extends Fragment {

    private Event event;
    private User user;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_buttons, container, false);

        Button accept = view.findViewById(R.id.button_accept_invite);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                // code
                event.getListManager().acceptInvite(user);
            }
        });

        Button decline = view.findViewById(R.id.button_decline_invite);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.getListManager().declineInvite(user);
            }
        });



        return view;
    }
}
