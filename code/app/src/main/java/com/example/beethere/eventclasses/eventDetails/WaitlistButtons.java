package com.example.beethere.eventclasses.eventDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.google.android.material.snackbar.Snackbar;

public class WaitlistButtons extends Fragment {

//    private Event event;
//    private User user;
//
//    public Event getEvent() {
//        return event;
//    }
//
//    public void setEvent(Event event) {
//        this.event = event;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//
//        View view = inflater.inflate(R.layout.fragment_waitlist_buttons, container, false);
//
//        // Join Event
//        ToggleButton waitlistButton = view.findViewById(R.id.button_waitlist);
//
//
//
//
//
//        waitlistButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // if Device ID does not have an account
//                // dialog fragment
//
//                // toggle change state unable to be done if user cannot join waitlist bc of max
//                if (event.getListManager().getMaxWaitlist() > event.getListManager().waitlistSize()) {
//                    // TODO
//                }
//
//
//
//
//
//                // button toggle results
//                if (waitlistButton.isChecked() && event.getListManager().getMaxWaitlist() > event.getListManager().waitlistSize()){
//                    // TODO
//                    // add user to event waitlist
//                    // get user from device ID
//                    event.getListManager().addWaitlist(user);
//                    Snackbar
//                            .make(view, "You have joined the waitlist!", Snackbar.LENGTH_SHORT)
//                            .show();
//                }
//                else {
//                    // TODO
//                    // confirmation of removal diaglog fragment
//                    // get user from device ID
//                    event.getListManager().removeWaitlist(user);
//                    Snackbar
//                            .make(view, "", Snackbar.LENGTH_SHORT)
//                            .show();
//                }
//
//
//
//
//
//            }
//        });
//
//
//
//        return view;
//    }
}
