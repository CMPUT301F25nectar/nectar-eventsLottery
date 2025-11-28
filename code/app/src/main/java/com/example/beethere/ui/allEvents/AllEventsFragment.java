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
import com.example.beethere.adapters.EventsAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


public class AllEventsFragment extends Fragment {

    private ArrayList<Event> eventList;
    private ArrayList<Event> displayedList;
    private EventsAdapter eventsAdapter;
    DateTimeFormatter dateFormatter;
    DateTimeFormatter timeFormatter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);

        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView search = view.findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchEvents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchEvents(newText);
                return true;
            }
        });

        ImageButton filter = view.findViewById(R.id.button_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filter dialog fragment





                /*displayedList.clear();
                filterDate(someDate, someDate, "reg");
                filterDate(someDate, someDate, "eventDate");
                filterTime(someTime, someTime);
                filterTags(someTags);*/
            }
        });


        eventList = new ArrayList<Event>();
        displayedList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(getContext(), displayedList);
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
        LocalDate currentDate = LocalDate.now();
        DatabaseFunctions functions = new DatabaseFunctions();
        DatabaseCallback<ArrayList<Event>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                eventList.clear();
                for (Event event : result) {
                    if(currentDate.isBefore(event.convertRegEnd())) {
                        eventList.add(event);
                    }
                }
                displayedList.clear();
                displayedList.addAll(eventList);
                eventsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(Exception e) {
                Log.d("AllEvents", "All events fragment error getting events from database");
            }
        };
        functions.getEventsDB(Boolean.FALSE, callback);
    }

    private void searchEvents(String query) {
        String lowerQuery = query.toLowerCase().trim();

        displayedList.clear();
        if (lowerQuery.isEmpty()) {
            displayedList.addAll(eventList);
        } else {
            for (Event event : eventList) {
                String title = event.getTitle().toLowerCase();
                String description = event.getDescription().toLowerCase();
                if (title.contains(lowerQuery) || description.contains(lowerQuery)) {
                    displayedList.add(event);
                }
            }
        }
        eventsAdapter.notifyDataSetChanged();
    }

    private void filterDate(LocalDate filterStart, LocalDate filterEnd, String tag){
        LocalDate startDate;
        LocalDate endDate;

        for (Event event : eventList) {

            if (Objects.equals(tag, "reg")) {
                startDate = convertDate(event.getRegStart());
                endDate = convertDate(event.getRegEnd());
            } else {
                startDate = convertDate(event.getEventDateStart());
                endDate = convertDate(event.getEventDateEnd());
            }

            if (startDate.isAfter(filterStart) && endDate.isBefore(filterEnd)) {
                displayedList.add(event);
            }
        }

    }

    private void filterTime(LocalTime filterStart, LocalTime filterEnd){
        LocalTime startTime;
        LocalTime endTime;
        for (Event event : eventList) {

            startTime = convertTime(event.getEventTimeStart());
            endTime = convertTime(event.getEventTimeEnd());

            if (startTime.isAfter(filterStart) && endTime.isBefore(filterEnd)) {
                displayedList.add(event);
            }
        }

    }

    /*private void filterTags(ArrayList<String> filterTags){

        ArrayList<String> tags;
        for (Event event : eventList) {

            tags = event.getTags();

            for (String tag : filterTags) {

                tag = tag.toLowerCase();
                if (tags.contains(tag) && !displayedList.contains(event)) {
                    displayedList.add(event);
                }

            }

        }

    }*/

    public LocalDate convertDate(String stringDate) {
        return LocalDate.parse(stringDate, dateFormatter);
    }

    public LocalTime convertTime(String stringTime) {
        return LocalTime.parse(stringTime, timeFormatter);
    }
}