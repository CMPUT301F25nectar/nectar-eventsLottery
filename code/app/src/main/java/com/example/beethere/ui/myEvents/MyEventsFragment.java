package com.example.beethere.ui.myEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.User;
import com.example.beethere.device.DeviceIDViewModel;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;


public class MyEventsFragment extends Fragment {

    private MyEventsAdapter adapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    private DeviceIDViewModel deviceIDViewModel;
    private User currentUser;

    private DatabaseFunctions dbFunctions = new DatabaseFunctions();

    private TextView noEventsMessage;
    private ListView eventsListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        Button createEventButton = view.findViewById(R.id.createEventButton);
        noEventsMessage = view.findViewById(R.id.noEventsMessage);
        eventsListView = view.findViewById(R.id.myEventsList);

        adapter = new MyEventsAdapter(getContext(), eventList);
        eventsListView.setAdapter(adapter);

        deviceIDViewModel = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
        String deviceID = deviceIDViewModel.getDeviceID();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID)
                .get()
                .addOnSuccessListener(snap -> {
                    currentUser = snap.toObject(User.class);
                    loadCreatedEvents();
                });

        createEventButton.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(view);

            if (currentUser == null) {
                nav.navigate(R.id.myEventsToProfileCreation);
            } else if (currentUser.getViolation()) {
                Toast.makeText(getContext(), "Unable to create events with past organizer violations commited", Toast.LENGTH_SHORT).show();
            } else {
                nav.navigate(R.id.myEventsToCreateEvents);
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
                adapter.notifyDataSetChanged();

                if (eventList.isEmpty()) {
                    noEventsMessage.setVisibility(View.VISIBLE);
                } else {
                    noEventsMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}