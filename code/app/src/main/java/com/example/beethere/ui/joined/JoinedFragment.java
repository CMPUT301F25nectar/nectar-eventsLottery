package com.example.beethere.ui.joined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.example.beethere.eventclasses.EventsAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class JoinedFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joined, container, false);


        SearchView search = view.findViewById(R.id.searchView);

        ImageButton filter = view.findViewById(R.id.button_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filter dialog fragment
            }
        });

        ArrayList<Event> eventList = new ArrayList<>();

        Button waitlisted = view.findViewById(R.id.button_waitlisted);
        waitlisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button enrolled = view.findViewById(R.id.button_enrolled);
        enrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button history = view.findViewById(R.id.button_history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ListView events = view.findViewById(R.id.event_display);
        /*EventsAdapter eventAdapter = new EventsAdapter(getContext(), eventList);
        events.setAdapter(eventAdapter);
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavController nav = Navigation.findNavController(view);
                nav.navigate(R.id.allEventToEventDetails);

                EventDataViewModel event = new ViewModelProvider(getActivity()).get(EventDataViewModel.class);
                event.setEvent((Event) parent.getItemAtPosition(position));
            }
        });*/
        return view;
    }
}
