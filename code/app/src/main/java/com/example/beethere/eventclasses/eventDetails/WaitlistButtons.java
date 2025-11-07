package com.example.beethere.eventclasses.eventDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.ui.profile.ProfileDialogFragment;
import com.google.android.material.snackbar.Snackbar;

public class WaitlistButtons extends Fragment {

    private Event event;
    private User user;
    private Boolean userCreated;

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

    public Boolean getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(Boolean userCreated) {
        this.userCreated = userCreated;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waitlist_buttons, container, false);

        // Join Event
        ToggleButton waitlistButton = view.findViewById(R.id.button_waitlist);
        waitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if Device ID does not have an account
                // dialog fragment
                if(!userCreated){

                    Runnable action = new Runnable() {
                        @Override
                        public void run() {
                        }
                    };

                    ProfileDialogFragment.show(getParentFragmentManager(), action);
                }

                // button toggle results
                if (waitlistButton.isChecked() && event.getEntrantList().getMaxWaitlist() > event.getEntrantList().waitlistSize()){
                    event.getEntrantList().addWaitlist(user);
                    Snackbar
                            .make(view, "You have joined the waitlist!", Snackbar.LENGTH_SHORT)
                            .show();
                }
                else {
                    event.getEntrantList().removeWaitlist(user);
                    Snackbar
                            .make(view, "You have left the waitlist!", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        return view;
    }
}
