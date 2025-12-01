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

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.UserListManager;
import com.google.android.material.snackbar.Snackbar;

public class InviteButtons extends Fragment {

    private Event event;
    private User user;

    public void setEvent(Event event) {
        this.event = event;
    }
    public void setUser(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details_invite_buttons, container, false);

        UserListManager manager = new UserListManager(event);

        Button accept = view.findViewById(R.id.button_accept_invite);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.acceptInvite(user);
                showSnackbar(view, "You have accepted the invite!");
            }
        });

        Button decline = view.findViewById(R.id.button_decline_invite);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.declineInvite(user);
                showSnackbar(view, "You have declined the invite!");
            }
        });

        return view;
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
