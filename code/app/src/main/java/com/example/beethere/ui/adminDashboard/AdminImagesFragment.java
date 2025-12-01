package com.example.beethere.ui.adminDashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.adapters.AdminImagesAdapter;
import com.example.beethere.adapters.MyEventsAdapter;
import com.example.beethere.eventclasses.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminImagesFragment extends Fragment {

    private AdminImagesAdapter adapter;
    private ArrayList<Event> eventList = new ArrayList<>();
    private ListView imagesListView;
    private DatabaseFunctions dbFunctions = new DatabaseFunctions();

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_view_images, container, false);

        imagesListView = view.findViewById(R.id.imagesListView);

        adapter = new AdminImagesAdapter(getContext(), eventList);
        imagesListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchEvents();

        return view;
    }
    private void fetchEvents() {
        dbFunctions.getEventsDB(new DatabaseCallback<>() {
            @Override
            public void onCallback(ArrayList<Event> result) {
                eventList.clear();
                eventList.addAll(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e("AdminImagesFragment", "Error fetching events", e);
            }
        });
    }
}
