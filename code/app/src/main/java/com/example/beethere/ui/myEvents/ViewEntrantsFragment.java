package com.example.beethere.ui.myEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;

import java.util.ArrayList;

public class ViewEntrantsFragment extends Fragment {

    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_entrants, container, false);


        Button waitListButton = view.findViewById(R.id.waitListButton);
        Button invitedButton = view.findViewById(R.id.invitedButton);
        Button registeredButton = view.findViewById(R.id.registeredButton);
        ListView entrantList = view.findViewById(R.id.entrantList);

        //TODO
            //first, create the content view for invited, keep in mind, this is also going to have a dropdown menu to delete
            //then, create content view for registered and wait list
            //then, create adapters for each content view
            //then, in the adapter add the activity (this one)
            //qrcode display class
            //honestly just do the whole damn thing 


        return view;
    }

}
