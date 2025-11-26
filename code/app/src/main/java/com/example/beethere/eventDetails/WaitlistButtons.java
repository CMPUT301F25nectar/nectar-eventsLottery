package com.example.beethere.eventDetails;

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
import com.example.beethere.eventclasses.UserListManager;
import com.google.android.material.snackbar.Snackbar;

public class WaitlistButtons extends Fragment {

    private Event event;
    private User user;
    private Boolean userCreated;

    public void setEvent(Event event) {
        this.event = event;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setUserCreated(Boolean userCreated) {
        this.userCreated = userCreated;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details_waitlist_buttons, container, false);
        UserListManager manager = new UserListManager(event);

        Button waitlistButton = view.findViewById(R.id.button_waitlist);
        if (userCreated && event.inWaitlist(user)) {
            leaveButton(waitlistButton);
        }

        waitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!userCreated){
                    //TODO
                    // create profile dialog popup
                }
                else if (event.inWaitlist(user)) {
                    manager.removeWaitlist(user);
                    Snackbar
                            .make(view, "You have left the waitlist!", Snackbar.LENGTH_SHORT)
                            .show();
                    joinButton(waitlistButton);
                }
                else {
                    manager.addWaitlist(user);
                    Snackbar
                            .make(view, "You have joined the waitlist!", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
                            .show();
                    leaveButton(waitlistButton);
                }
            }
        });

        return view;
    }

    public void leaveButton(Button waitlistButton) {
        waitlistButton.setText(getContext().getString(R.string.leave));
        waitlistButton.setBackgroundColor(getContext().getColor(R.color.red));
    }

    public void joinButton(Button waitlistButton){
        waitlistButton.setText(getContext().getString(R.string.join_waitlist));
        waitlistButton.setBackgroundColor(getContext().getColor(R.color.yellow));
    }
}
