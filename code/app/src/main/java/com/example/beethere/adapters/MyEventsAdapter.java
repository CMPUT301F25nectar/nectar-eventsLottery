package com.example.beethere.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.eventDetails.QRCodeFragment;
import com.example.beethere.ui.myEvents.ConfirmDeleteFragment;

import java.util.ArrayList;

public class MyEventsAdapter extends ArrayAdapter<Event> {

    public MyEventsAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.content_my_events, parent, false)
                : convertView;

        Event event = getItem(position);
        if (event == null) return view;

        TextView title = view.findViewById(R.id.myEventsTitle);
        ImageView poster = view.findViewById(R.id.myEventsPoster);
        TextView enrollStart = view.findViewById(R.id.enrollStart);
        TextView enrollEnd = view.findViewById(R.id.enrollEnd);
        ImageButton optionsMenuButton = view.findViewById(R.id.optionsMenuButton);

        title.setText(event.getTitle());
        if (event.getPosterPath() != null) {
            Glide.with(getContext())
                    .load(event.getPosterPath()) // This is the download URL
                    //.placeholder(R.drawable.placeholder) // optional
                    //.error(R.drawable.error) // optional
                    .into(poster);
        }



        enrollStart.setText(event.getRegStart());
        enrollEnd.setText(event.getRegEnd());

        optionsMenuButton.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(getContext(), R.style.CustomPopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.getMenuInflater().inflate(R.menu.event_options_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.edit) {
                    if (getContext() instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        NavController nav = Navigation.findNavController(
                                activity.findViewById(R.id.nav_host_fragment)
                        );
                        Bundle bundle = new Bundle();
                        bundle.putString("eventID", event.getEventID());
                        nav.navigate(R.id.myEventsToEditEvents, bundle);
                    }
                    return true;
                } else if (id == R.id.delete) {
                    ConfirmDeleteFragment confirmDeleteFragment = new ConfirmDeleteFragment(event.getEventID());
                    if (getContext() instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        confirmDeleteFragment.setOnEventDeletedListener(this::removeEventById);
                        confirmDeleteFragment.show(activity.getSupportFragmentManager(), "goBackDialog");
                    }
                    return true;
                } else if (id == R.id.qrcode) {
                    QRCodeFragment qrFragment = QRCodeFragment.newInstance(event.getEventID(), Boolean.TRUE);
                    if (getContext() instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        qrFragment.show(activity.getSupportFragmentManager(), "qrCodeDialog");
                    }
                    return true;
                } else if (id == R.id.entrants) {
                    if (getContext() instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        NavController nav = Navigation.findNavController(
                                activity.findViewById(R.id.nav_host_fragment)
                        );

                        Bundle bundle = new Bundle();
                        bundle.putString("eventID", event.getEventID());
                        nav.navigate(R.id.myEventsToViewEntrants, bundle);
                    }
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        return view;
    }

    public void removeEventById(String eventID) {
        for (int i = 0; i < getCount(); i++) {
            Event event = getItem(i);
            if (event != null && event.getEventID().equals(eventID)) {
                remove(event);
                notifyDataSetChanged();
                break;
            }
        }
    }
}