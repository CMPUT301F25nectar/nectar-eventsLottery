package com.example.beethere.eventclasses.eventDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

        Button waitlistButton = view.findViewById(R.id.button_waitlist);
        joinButton(waitlistButton);

        UserListManager manager = new UserListManager(event);
        if (userCreated && manager.inWaitlist(user)) {
            leaveButton(waitlistButton);
        }

        waitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!userCreated){
                    NavController nav = Navigation.findNavController(view);
                    nav.navigate(R.id.eventDetailsToProfileCreation);

                }
                else if (manager.inWaitlist(user)) {
                    manager.removeWaitlist(user);
                    showSnackbar(view, "You have left the waitlist!");
                    joinButton(waitlistButton);
                }
                else {
                    manager.addWaitlist(user);
                    showSnackbar(view, "You have joined the waitlist!");
                    leaveButton(waitlistButton);
                }
            }
        });

        return view;
    }

    public void leaveButton(Button waitlistButton) {
        waitlistButton.setText(getContext().getString(R.string.leave));
        waitlistButton.setSelected(false);
    }

    public void joinButton(Button waitlistButton){
        waitlistButton.setText(getContext().getString(R.string.join_waitlist));
        waitlistButton.setSelected(true);
    }

    public void showSnackbar(View view, String text){
        Snackbar snackbar = Snackbar.make(view,text, Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.geoLocReq)
                .setBackgroundTint(getContext().getColor(R.color.dark_brown))
                .setTextColor(getContext().getColor(R.color.yellow));
        View snackbarView = snackbar.getView();
        TextView snackbarText = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        snackbarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbarText.setTextSize(20);
        snackbarText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.work_sans_semibold));
        snackbar.show();
    }
}
