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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.example.beethere.eventclasses.eventDetails.QRCodeFragment;
import com.example.beethere.ui.myEvents.ConfirmDeleteFragment;

import java.util.ArrayList;

public class EventsAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Event event;
    private Boolean admin;

    /**
     **  the context of the activity where the events are being showcased
     * @param events the events made by a organizer associated with a unique deviceID
     */
    public EventsAdapter(Context context, ArrayList<Event> events){ // context needed, as fragment isnt a context(activity)
        super(context, 0, events);
        this.events = events;
        this.admin = Boolean.FALSE;
    }
    public EventsAdapter(Context context, ArrayList<Event> events, Boolean admin){
        super(context, 0, events);
        this.events = events;
        this.admin = admin;
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

        event = events.get(position);
        TextView title = view.findViewById(R.id.EventsTitle);
        ImageView poster = view.findViewById(R.id.EventsPoster);
        TextView enrollStart = view.findViewById(R.id.EventEnrollStart);

        if (event != null) {
            title.setText(event.getTitle());
            enrollStart.setText(String
                    .format(
                            getContext().getString(R.string.event_date),
                            event.getRegStart(),
                            event.getRegEnd()));

            if (event.getPosterPath() != null) {
                Glide.with(getContext())
                        .load(event.getPosterPath())
                        .placeholder(R.drawable.placeholder_event_poster)
                        .error(R.drawable.placeholder_event_poster)
                        .into(poster);
            }
        }


        if(admin){
         displayAdmin(view);
        }

        return view;
    }

    public void displayAdmin(View view){
        ImageButton optionsMenuButton = view.findViewById(R.id.admin_options_menu_button);
        optionsMenuButton.setVisibility(View.VISIBLE);

        optionsMenuButton.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(getContext(), R.style.CustomPopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.getMenuInflater().inflate(R.menu.admin_event_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();

                if (id == R.id.admin_event_details) {
                    if (getContext() instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        EventDataViewModel eventModel = new ViewModelProvider(activity).get(EventDataViewModel.class);
                        eventModel.setEvent(event);
                        NavController nav = Navigation.findNavController(
                                activity.findViewById(R.id.nav_host_fragment)
                        );
                        nav.navigate(R.id.admin_to_event_details);
                    }
                    return true;
                } else if (id == R.id.admin_qrcode) {
                    QRCodeFragment qrFragment = QRCodeFragment.newInstance(event.getEventID(), Boolean.FALSE);
                    if (getContext() instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        qrFragment.show(activity.getSupportFragmentManager(), "qrCodeDialog");
                    }
                    return true;
                } else if (id == R.id.admin_delete_event) {
                    if (getContext() instanceof AppCompatActivity) {
                        ConfirmDeleteFragment confirmDeleteFragment = new ConfirmDeleteFragment(event.getEventID());
                        AppCompatActivity activity = (AppCompatActivity) getContext();

                        confirmDeleteFragment.setOnEventDeletedListener(this::removeEventById);// updating adapter
                        confirmDeleteFragment.show(activity.getSupportFragmentManager(), "goBackDialog");// showing the dialog
                    }
                    return true;
                } else if (id == R.id.admin_delete_organizer) {
                    if (getContext() instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        NavController nav = Navigation.findNavController(
                                activity.findViewById(R.id.nav_host_fragment)
                        );
                        // TODO



                    }
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }


    // updating adapter to remove event stuff
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
