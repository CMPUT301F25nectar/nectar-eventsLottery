package com.example.beethere.ui.myEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.User;
import com.example.beethere.adapters.MyEventsAdapter;
import com.example.beethere.device.DeviceIDViewModel;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyEventsFragment extends Fragment {

    private MyEventsAdapter adapter;
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Event> displayedList = new ArrayList<>();

    private DeviceIDViewModel deviceIDViewModel;
    private User currentUser;

    private DatabaseFunctions dbFunctions = new DatabaseFunctions();
    private boolean hasAnyEvents = false;

    private TextView noEventsMessage1, noEventsMessage2, searchNoEventsMessage;
    private ListView eventsListView;
    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        AppCompatButton createEventButton = view.findViewById(R.id.createEventButton);
        noEventsMessage1 = view.findViewById(R.id.noEventsMessage1);
        noEventsMessage2 = view.findViewById(R.id.noEventsMessage2);
        searchNoEventsMessage = view.findViewById(R.id.searchNoEventsMessage);
        eventsListView = view.findViewById(R.id.myEventsList);
        searchView = view.findViewById(R.id.myEventsSearch);

        adapter = new MyEventsAdapter(getContext(), displayedList);
        eventsListView.setAdapter(adapter);

        deviceIDViewModel = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
        String deviceID = deviceIDViewModel.getDeviceID();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID)
                .get()
                .addOnSuccessListener(snap -> {

                    if (snap.exists()) {
                        currentUser = snap.toObject(User.class);
                        loadCreatedEvents();
                    } else {
                        currentUser = null;
                    }
                });


        createEventButton.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(view);

            if (currentUser == null) {
                nav.navigate(R.id.myEventsToProfileCreation);
            } else if (Boolean.TRUE.equals(currentUser.getViolation())) {
                Toast.makeText(getContext(), "Unable to create events with past organizer violations committed", Toast.LENGTH_SHORT).show();
            } else {
                nav.navigate(R.id.myEventsToCreateEvents);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        return view;
    }

    private void loadCreatedEvents() {
        if (currentUser == null) return;

        dbFunctions.getCreatedEventsDB(currentUser, new DatabaseCallback<ArrayList<Event>>() {
            @Override
            public void onCallback(ArrayList<Event> events) {
                eventList.clear();
                eventList.addAll(events);

                displayedList.clear();
                displayedList.addAll(eventList);
                adapter.notifyDataSetChanged();

                hasAnyEvents = !eventList.isEmpty();

                updateNoEventsMessage();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterEvents(String query) {
        String lowerQuery = query.toLowerCase().trim();

        displayedList.clear();
        if (lowerQuery.isEmpty()) {
            displayedList.addAll(eventList);
        } else {
            for (Event event : eventList) {
                if (event.getTitle().toLowerCase().contains(lowerQuery)) {
                    displayedList.add(event);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateNoEventsMessage();
    }

    private void updateNoEventsMessage() {
        String query = searchView.getQuery().toString().trim();

        if (displayedList.isEmpty()) {
            if (!hasAnyEvents) {
                noEventsMessage1.setVisibility(View.VISIBLE);
                noEventsMessage2.setVisibility(View.VISIBLE);
            } else if (!query.isEmpty()) {
                searchNoEventsMessage.setVisibility(View.VISIBLE);
            }
        } else {
            noEventsMessage1.setVisibility(View.GONE);
            noEventsMessage2.setVisibility(View.GONE);
            searchNoEventsMessage.setVisibility(View.GONE);
        }
    }
}

