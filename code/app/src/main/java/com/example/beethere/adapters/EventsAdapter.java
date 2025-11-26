package com.example.beethere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;

import java.util.ArrayList;

public class EventsAdapter extends ArrayAdapter<Event> {

    ArrayList<Event> events;

    /**
     **  the context of the activity where the events are being showcased
     * @param events the events made by a organizer associated with a unique deviceID
     */
    public EventsAdapter(Context context, ArrayList<Event> events){ // context needed, as fragment isnt a context(activity)
        super(context, 0, events);
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_all_events, parent, false);
        } else {
            view = convertView;
        }

        Event event = events.get(position);
        TextView title = view.findViewById(R.id.EventsTitle);
        ImageView poster = view.findViewById(R.id.EventsPoster);
        TextView enrollStart = view.findViewById(R.id.EventEnrollStart);
        TextView enrollEnd = view.findViewById(R.id.EventEnrollEnd);

        if (event != null) {
            title.setText(event.getTitle());
            // poster
            enrollStart.setText(event.getRegStart());
            enrollEnd.setText(event.getRegEnd());
        }

        return view;
    }
}
