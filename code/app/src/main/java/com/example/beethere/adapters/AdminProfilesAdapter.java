package com.example.beethere.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminProfilesAdapter extends ArrayAdapter<User> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> users;
    private Context context;
    private User user;

    public AdminProfilesAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.content_admin_profiles, parent, false)
                : convertView;

        user = getItem(position);
        if (user == null) return view;

        TextView userName = view.findViewById(R.id.name);
        TextView userType = view.findViewById(R.id.userType);
        ImageButton deleteUser = view.findViewById(R.id.deleteUser);
        SearchView searchBar = view.findViewById(R.id.all_profiles_search_view);
        ImageButton filer = view.findViewById(R.id.button_filter);

        userName.setText(user.getName());

        if (Boolean.TRUE.equals(user.getOrganizer()) && !Boolean.TRUE.equals(user.getAdmin())) {
            userType.setText("Organizer");
            userType.setSelected(true);
        } else if (!Boolean.TRUE.equals(user.getOrganizer()) && !Boolean.TRUE.equals(user.getAdmin())) {
            userType.setText("Entrant");
            userType.setSelected(false);
        }


        final User currentUser = getItem(position);
        deleteUser.setOnClickListener(v -> { // todo, maybe a popup, to either set violation or delete organizer entirely, so just update db from here
            if (currentUser == null) return;

            // Use DatabaseFunctions to safely delete user
            DatabaseFunctions dbFunctions = new DatabaseFunctions();
            dbFunctions.deleteUserDB(currentUser);

            // Update UI
            users.remove(position);
            notifyDataSetChanged();
            showSnackbar(v, "User deleted");
        });

        return view;
    }

    public void showSnackbar(View view, String text){
        Snackbar snackbar = Snackbar.make(view,text, Snackbar.LENGTH_SHORT)
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
