package com.example.beethere.ui.myEvents;

import android.content.Context;
import android.graphics.Bitmap;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.eventDetails.QRCodeFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class MyEventsAdapter extends ArrayAdapter<Event> {

    ArrayList<Event> events;

    public MyEventsAdapter(Context context, ArrayList<Event> events) {
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
        TextView title = view.findViewById(R.id.myEventsTitle);
        ImageView poster = view.findViewById(R.id.myEventsPoster);
        TextView enrollStart = view.findViewById(R.id.enrollStart);
        TextView enrollEnd = view.findViewById(R.id.enrollEnd);

        ImageButton optionsMenuButton = view.findViewById(R.id.optionsMenuButton);

        title.setText(event.getTitle());
        String posterPath = event.getPosterPath();

        poster.setImageURI(Uri.parse(posterPath));
        title.setText(event.getTitle());
        enrollStart.setText(event.getRegStart());
        enrollEnd.setText(event.getRegEnd());


        // Set options menu
        optionsMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
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
                    QRCodeFragment qrFragment = QRCodeFragment.newInstance(event.getEventID());

                    if (getContext() instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        qrFragment.show(activity.getSupportFragmentManager(), "qrCodeDialog");
                    }
                    return true;
                } else if (id == R.id.entrants) {
                    NavController nav = Navigation.findNavController(view);
                    nav.navigate(R.id.myEventsToViewEntrants);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        return view;
    }
}




