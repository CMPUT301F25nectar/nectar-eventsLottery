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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminImagesAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://beethere-images");

    public AdminImagesAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
        this.events = events;  // <-- FIX
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

        Glide.with(getContext()).clear(eventPoster); // clear old image
        if (event.getPosterPath() != null && !event.getPosterPath().isEmpty()) {
            Glide.with(getContext())
                    .load(event.getPosterPath())
                    .placeholder(R.drawable.placeholder_event_poster)
                    .error(R.drawable.placeholder_event_poster)
                    .into(eventPoster);
        } else {
            eventPoster.setImageResource(R.drawable.placeholder_event_poster);
        }


        trash.setOnClickListener(v -> {
            eventPoster.setAlpha(0.7f);
            StorageReference PosterRef = storage.getReferenceFromUrl(event.getPosterPath());
            PosterRef.delete()
                    .addOnSuccessListener(a -> FirebaseFirestore.getInstance()
                            .collection("events")
                            .document(event.getEventID())
                            .update("posterPath", null)
                            .addOnSuccessListener(update -> {
                                removePoster(event.getEventID());
                                showSnackbar(v, "Poster deleted successfully");
                            })
                            .addOnFailureListener(e -> {
                                eventPoster.setAlpha(1f);
                                Log.e("ImageFromDatabase", "Failed to update event in database");//ehhhhhhhhhhh
                            }))
                    .addOnFailureListener(e -> {
                        eventPoster.setAlpha(1f);
                        showSnackbar(v, "Failed to delete poster");
                    });
        });



        return view;
    }

    public void removePoster(String eventID) {
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            if (e.getEventID().equals(eventID)) {
                events.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }


    public void showSnackbar(View view, String text){
        if (view == null) return;
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getContext().getColor(R.color.dark_brown))
                .setTextColor(getContext().getColor(R.color.yellow));

        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        snackbarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbarText.setTextSize(20);
        snackbarText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.work_sans_semibold));

        snackbar.show();
    }
}
