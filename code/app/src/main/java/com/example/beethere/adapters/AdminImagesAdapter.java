package com.example.beethere.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.ui.myEvents.ConfirmDeleteFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminImagesAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://beethere-images");

    public AdminImagesAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.content_admin_images, parent, false)
                : convertView;

        Event event = getItem(position);
        if (event == null) return view;

        TextView eventTitle = view.findViewById(R.id.eventTitle);
        ImageView eventPoster = view.findViewById(R.id.eventsPoster);
        ImageButton trash = view.findViewById(R.id.trash);

        eventTitle.setText(event.getTitle());
        if (event.getPosterPath() != null) {
            Glide.with(getContext())
                    .load(event.getPosterPath())
                    .placeholder(R.drawable.placeholder_event_poster)
                    .error(R.drawable.placeholder_event_poster)
                    .into(eventPoster);
        }

        trash.setOnClickListener(v -> {
            eventPoster.setAlpha(0.7f);
            StorageReference PosterRef = storage.getReferenceFromUrl(event.getPosterPath());
            PosterRef.delete();
            removePoster(event.getEventID());
        });

        return super.getView(position, convertView, parent);
    }

    public void removePoster(String eventID) {
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
