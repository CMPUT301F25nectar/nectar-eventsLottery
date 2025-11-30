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
import android.widget.Toast;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MyEventsAdapter extends ArrayAdapter<Event> {

    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     *
     * @param context context of adapter
     * @param events events associated with organizer
     */
    public MyEventsAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
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
        LocalDate eventEnd  = LocalDate.parse(event.getEventDateEnd(), dateFormatter);

        title.setText(event.getTitle());
        if (event.getPosterPath() != null) {
            Glide.with(getContext())
                    .load(event.getPosterPath())
                    .placeholder(R.drawable.placeholder_event_poster)
                    .error(R.drawable.placeholder_event_poster)
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
                    if (currentDate.isAfter(eventEnd)) {
                        Toast.makeText(getContext(), "Unable to edit event: Event period ended.", Toast.LENGTH_LONG).show();
                    } else {
                        if (getContext() instanceof AppCompatActivity) {
                            AppCompatActivity activity = (AppCompatActivity) getContext();
                            NavController nav = Navigation.findNavController(
                                    activity.findViewById(R.id.nav_host_fragment)
                            );
                            Bundle bundle = new Bundle();
                            bundle.putString("eventID", event.getEventID());
                            nav.navigate(R.id.myEventsToEditEvents, bundle);
                        }
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

    /**
     *
     * @param eventID takes eventID to update items in the adapter
     */
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