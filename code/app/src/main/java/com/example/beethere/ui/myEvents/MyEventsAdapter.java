package com.example.beethere.ui.myEvents;

import com.example.beethere.eventclasses.Event;


import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;


public class MyEventsAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    ImageButton optionsMenuButton;// format date

    /**
     **  the context of the activity where the events are being showcased
     * @param events the events made by a organizer associated with a unique deviceID
     */
    public MyEventsAdapter(Context context, ArrayList<Event> events){ // context needed, as fragment isnt a context(activity)
        super(context, 0, events);
        this.events = events;
       // this.context = context;
    }

    /**
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return view
     */

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_my_events, parent, false);
        }

        optionsMenuButton = view.findViewById(R.id.optionsMenuButton);

        Event event = events.get(position);
        TextView title = view.findViewById(R.id.myEventsTitle);
        ImageView poster = view.findViewById(R.id.myEventsPoster);
        TextView enrollStart = view.findViewById(R.id.enrollStart);
        TextView enrollEnd = view.findViewById(R.id.enrollEnd);
        LocalDate regStart = event.getRegStart();
        LocalDate regEnd = event.getRegEnd();

        //set the objects information using getters
        title.setText(event.getTitle());
        poster.setImageResource(event.getPoster());
        enrollStart.setText(formatter.format(regStart));
        enrollEnd.setText(formatter.format(regEnd));

        optionsMenuButton.setOnClickListener(v-> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.event_options_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.edit) {
                    // TODO: handle action for this
                    return true;
                } else if (id == R.id.delete) {
                    // TODO: handle action for this
                    return true;
                } else if (id == R.id.qrcode) {
                    // TODO: handle action for this
                    return true;
                } else if (id == R.id.entrants) {
                    // TODO: handle action for this
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        return view;
    }
}



