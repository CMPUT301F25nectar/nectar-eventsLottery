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


public class AllEventsFragment extends Fragment implements FilterEventsDialog.FilterDialogListener {

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

        SearchView search = view.findViewById(R.id.all_events_search_view);
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
                FilterEventsDialog dialog = new FilterEventsDialog(AllEventsFragment.this);
                dialog.show(getParentFragmentManager(), "filterEvents");



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
    @Override
    public void onFilterApplied(String regStart, String regEnd, String eventStart, String eventEnd,
                                String timeStart, String timeEnd){
        displayedList.clear();

        //applying filters
        for (Event event :eventList) {
            boolean passesFilter = true;

            //by registration dates
            if (!regStart.isEmpty() && !regEnd.isEmpty()){
                LocalDate eventRegStart = convertDate(event.getRegStart());
                LocalDate eventRegEnd = convertDate(event.getRegEnd());
                LocalDate filterRegStart = convertDate(regStart);
                LocalDate filterRegEnd = convertDate(regEnd);

                if (eventRegStart.isBefore(filterRegStart) || eventRegEnd.isAfter(filterRegEnd)) {
                    passesFilter = false;
                }
            }
            //by event dates
            if (passesFilter && !eventStart.isEmpty() && !eventEnd.isEmpty()) {
                LocalDate eventDateStart = convertDate(event.getEventDateStart());
                LocalDate eventDateEnd = convertDate(event.getEventDateEnd());
                LocalDate filterEventStart = convertDate(eventStart);
                LocalDate filterEventEnd = convertDate(eventEnd);

                if (eventDateStart.isBefore(filterEventStart) || eventDateEnd.isAfter(filterEventEnd)) {
                    passesFilter = false;
                }
            }

            //by time
            if (passesFilter && !timeStart.isEmpty() && !timeEnd.isEmpty()) {
                LocalTime eventTimeStart = convertTime(event.getEventTimeStart());
                LocalTime eventTimeEnd = convertTime(event.getEventTimeEnd());
                LocalTime filterTimeStart = convertTime(timeStart);
                LocalTime filterTimeEnd = convertTime(timeEnd);

                if (eventTimeStart.isBefore(filterTimeStart) || eventTimeEnd.isAfter(filterTimeEnd)) {
                    passesFilter = false;
                }
            }

            if (passesFilter) {
                displayedList.add(event);
            }
            //by tags
//            if (passesFilter && !tags.isEmpty()){
//                String tagArray = tags.split(",");
//                boolean hasMatchingTag = false;
//
//                ArrayList<String> eventTags = event.getTitle().toLowerCase();
//                String
//            }

        }
        eventsAdapter.notifyDataSetChanged();

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
        functions.getEventsDB(callback);
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