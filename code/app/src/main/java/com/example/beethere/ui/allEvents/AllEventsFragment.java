package com.example.beethere.ui.allEvents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.example.beethere.eventclasses.EventsAdapter;

import java.time.LocalDate;
import java.util.ArrayList;


public class AllEventsFragment extends Fragment {

    private ArrayList<Event> eventList;
    private EventsAdapter eventsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView search = view.findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEvents(newText);
                return true;
            }
        });

        ImageButton filter = view.findViewById(R.id.button_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filter dialog fragment
            }
        });


        eventList = new ArrayList<Event>();
        eventsAdapter = new EventsAdapter(getContext(), eventList);
        ListView events = view.findViewById(R.id.event_display);
        events.setAdapter(eventsAdapter);
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavController nav = Navigation.findNavController(view);
                nav.navigate(R.id.allEventToEventDetails);

                EventDataViewModel event = new ViewModelProvider(getActivity()).get(EventDataViewModel.class);
                event.setEvent((Event) parent.getItemAtPosition(position));
            }
        });
        loadEvents();
    }

    public void loadEvents(){

        DatabaseFunctions functions = new DatabaseFunctions();
        DatabaseCallback<ArrayList<Event>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                eventList.addAll(result);
                eventsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(Exception e) {
                Log.d("AllEvents", "All events fragment error getting events from database");
            }
        };
        functions.getEventsDB(callback);
        LocalDate currentDate = LocalDate.now();
        eventList.removeIf(event -> currentDate.isAfter(event.convertRegEnd()));

    }

    private void filterEvents(String query) {
        String lowerQuery = query.toLowerCase().trim();

        eventList.clear();
        if (lowerQuery.isEmpty()) {
            eventList.addAll(eventList);
        } else {
            for (Event event : eventList) {
                if (event.getTitle().toLowerCase().contains(lowerQuery)) {
                    eventList.add(event);
                }
            }
        }

        eventsAdapter.notifyDataSetChanged();
    }

}