package com.example.beethere.ui.adminDashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.adapters.AdminImagesAdapter;
import com.example.beethere.adapters.EventsAdapter;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;

import java.time.LocalDate;
import java.util.ArrayList;

public class AdminEventsFragment extends Fragment {

    private ArrayList<Event> eventList;
    private ArrayList<Event> displayedList;
    private EventsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_view_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        NavController nav = Navigation.findNavController(view);

        Button backButton = view.findViewById(R.id.admin_event_back_button);
        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        SearchView search = view.findViewById(R.id.admin_events_search);
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

        eventList = new ArrayList<>();
        displayedList = new ArrayList<>();
        adapter = new EventsAdapter(getContext(), displayedList, Boolean.TRUE);
        ListView events = view.findViewById(R.id.admin_event_display);
        events.setAdapter(adapter);
        events.setOnItemClickListener((parent, view1, position, id) -> {
            EventDataViewModel event = new ViewModelProvider(requireActivity()).get(EventDataViewModel.class);
            event.setEvent((Event) parent.getItemAtPosition(position));

            // TODO
            nav.navigate(R.id.admin_to_event_details);
        });
        loadEvents();


        super.onViewCreated(view, savedInstanceState);
    }

    public void loadEvents(){
        DatabaseFunctions functions = new DatabaseFunctions();
        DatabaseCallback<ArrayList<Event>> callback = new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                eventList.clear();
                eventList.addAll(result);
                displayedList.clear();
                displayedList.addAll(eventList);
                adapter.notifyDataSetChanged();
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
        adapter.notifyDataSetChanged();
    }
}
