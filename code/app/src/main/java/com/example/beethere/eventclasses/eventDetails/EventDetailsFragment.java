package com.example.beethere.eventclasses.eventDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 *
 */
public class EventDetailsFragment extends Fragment {

    private Event event;
    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        // Event Name display
        TextView name = view.findViewById(R.id.text_event_name);
        name.setText(event.getName());

        // Event organizer display
        TextView organizer = view.findViewById(R.id.text_host_name);
        organizer.setText( String
                .format(
                        String.valueOf(R.string.event_organizer),
                        event.getOrganizer().getName())
        );

        // Event Description Display
        TextView description = view.findViewById(R.id.text_description);
        description.setText(event.getDescription());

        // Event Geoloc Req Display
        TextView geoLocReq = view.findViewById(R.id.text_geoloc_req);
        if (event.getGeoloc()) {
            geoLocReq.setText(R.string.yes);
        } else {
            geoLocReq.setText(R.string.no);
        }

        // Event Date display
        TextView date = view.findViewById(R.id.text_event_date);
        date.setText(String
                .format(
                        String.valueOf(R.string.event_date),
                        event.getEventDateStart().toString(),
                        event.getEventDateEnd().toString())
        );

        // Event Time display
        TextView time = view.findViewById(R.id.text_event_time);
        time.setText(String
                .format(
                        String.valueOf(R.string.event_time),
                        event.getEventTimestart().toString(),
                        event.getEventTimeEnd().toString())
        );

        // Max Registered display
        TextView maxEnroll = view.findViewById(R.id.text_max_enroll);
        maxEnroll.setText(event.getListManager().getMaxRegistered());

        // Number of people in waitlist
        TextView waitlist = view.findViewById(R.id.text_waitlist);
        waitlist.setText(event.getListManager().waitlistSize().toString());

        // TODO
        // event image set up


        // SET LISTENERS
        // Go back to event list
        FloatingActionButton fab = view.findViewById(R.id.button_back);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                onDestroy();
                // or
                // getParentFragmentManager().beginTransaction().remove(EventDetailsFragment.this).commitAllowingStateLoss();
            }
        });

        ImageButton imageButton = view.findViewById(R.id.button_QR);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display image dialog fragment?

            }
        });


        // need to get user from device id or database or whatever
        if (/*accepted invite*/) {
            // TODO
            // set text "enrolled"

        } else if (/*declined invite*/) {
            // TODO
            // set text "waitlist period ended"
        }
        else if (event.getListManager().inInvite(user)) {
            // TODO
            InviteButtons button = new InviteButtons();
            button.setEvent(event);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.button_layout, button).commit();
        } else if (/*waitlist join date passed*/) {
            // TODO
            // set text "waitlist period ended
        }
        else {
            WaitlistButtons button = new WaitlistButtons();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.button_layout, button).commit();
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentFragmentManager().beginTransaction().remove(EventDetailsFragment.this).commitAllowingStateLoss();
    }
}
