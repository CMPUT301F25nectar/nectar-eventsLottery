package com.example.beethere.ui.myEvents;

import android.content.Context;
import android.net.Uri;
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

import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MyEventsAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public MyEventsAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_my_events, parent, false);
        }

        Event event = events.get(position);

        TextView title = view.findViewById(R.id.myEventsTitle);
        ImageView poster = view.findViewById(R.id.myEventsPoster);
        TextView enrollStart = view.findViewById(R.id.enrollStart);
        TextView enrollEnd = view.findViewById(R.id.enrollEnd);
        ImageButton optionsMenuButton = view.findViewById(R.id.optionsMenuButton);

        // Set event title
        title.setText(event.getTitle());

        // Set poster image from URI if available
        String posterPath = event.getPosterPath();
        if (posterPath != null && !posterPath.isEmpty()) {
            poster.setImageURI(Uri.parse(posterPath));
        } else {
            poster.setImageResource(R.drawable.ic_profile); // fallback image
        }

        // Set enrollment dates
        if (event.getRegStart() != null) {
            enrollStart.setText(event.getRegStart().format(dateFormatter));
        }
        if (event.getRegEnd() != null) {
            enrollEnd.setText(event.getRegEnd().format(dateFormatter));
        }

        // Set options menu
        optionsMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.event_options_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.edit) {
                    // handle edit
                    return true;
                } else if (id == R.id.delete) {
                    // handle delete
                    return true;
                } else if (id == R.id.qrcode) {
                    // handle QR code
                    return true;
                } else if (id == R.id.entrants) {
                    // handle entrants
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        return view;
    }
}




