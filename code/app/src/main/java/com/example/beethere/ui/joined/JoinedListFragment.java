package com.example.beethere.ui.joined;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.adapters.EventsAdapter;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;

import java.util.ArrayList;

public class JoinedListFragment extends Fragment {

    private ArrayList<Event> eventList = new ArrayList<>();
    private EventsAdapter eventsAdapter;

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList.clear();
        this.eventList.addAll(eventList);
    }

    public EventsAdapter getEventsAdapter() {
        return eventsAdapter;
    }

    public void setEventsAdapter(EventsAdapter eventsAdapter) {
        this.eventsAdapter = eventsAdapter;
    }

    public void updateEventList(ArrayList<Event> eventList){
        this.eventList.clear();
        this.eventList.addAll(eventList);
        this.eventsAdapter.notifyDataSetChanged();

        ArrayList<Event> tempList = eventsAdapter.getCurrentEventList();

        for (Event event : tempList){
            Log.d("JoinedList", event.getEventID());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joined_list_display, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView eventsDisplay = view.findViewById(R.id.joined_event_display_2_try);
        eventsAdapter = new EventsAdapter(getContext(), eventList);
        eventsDisplay.setAdapter(eventsAdapter);


        eventsDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavController nav = Navigation.findNavController(view);
                nav.navigate(R.id.joinedToEventDetails);

                EventDataViewModel event = new ViewModelProvider(getActivity()).get(EventDataViewModel.class);
                event.setEvent((Event) parent.getItemAtPosition(position));
            }
        });


    }
}
