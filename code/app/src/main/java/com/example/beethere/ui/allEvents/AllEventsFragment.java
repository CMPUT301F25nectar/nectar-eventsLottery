package com.example.beethere.ui.allEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.eventDetails.EventDetailsFragment;

import java.util.ArrayList;


public class AllEventsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);


        SearchView search = view.findViewById(R.id.searchView);

        ImageButton filter = view.findViewById(R.id.button_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filter dialog fragment
            }
        });

        ListView events = view.findViewById(R.id.event_display);
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavController nav = Navigation.findNavController(view);
                        nav.navigate(R.id.myEventsToCreateEvents);

                /*EventDetailsFragment fragment = new EventDetailsFragment();
                //Event event = (Event) parent.getItemAtPosition(position);
                //fragment.setEvent(event);
                fragment.setEvent((Event) parent.getItemAtPosition(position));
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.someId, fragment).commit();*/
            }
        });



        return view;
    }
}