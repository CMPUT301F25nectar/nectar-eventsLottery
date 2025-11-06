package com.example.beethere.ui.myEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;

public class CreateEventFragment2 extends Fragment {
    SwitchCompat geoLocation;
    SwitchCompat randomSelect;
    SwitchCompat maxWaitlist;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_pg2, container, false);

        geoLocation = view.findViewById(R.id.geoLocSwitch);
        randomSelect = view.findViewById(R.id.randomSelectSwitch);
        maxWaitlist = view.findViewById(R.id.maxWaitSwitch);

        Button completeButton = view.findViewById(R.id.completeButton);

//        completeButton.setOnClickListener(v -> {
//            Event newEvent = new Event(
//                    organizer, // how to grab this
//                    titleInput,
//                    descInput,
//                    imageURL,
//                    regStartDate,
//                    regEndDate,
//                    eventStartDate,
//                    eventEndDate,
//                    startTime,
//                    endTime,
//                    maxAttendeesInt,
//                    getLocation,
//                    entrantList = entrantList;
//                    autoRandomSelection
//            );
//
//
//        });
// TODO: save the new event in the firebase db
        return view;
    }

//    public boolean verify () {
//        //here, what are we even verifying here?
//    }

}
