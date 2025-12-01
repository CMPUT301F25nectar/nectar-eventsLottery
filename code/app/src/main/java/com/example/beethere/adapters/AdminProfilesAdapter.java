package com.example.beethere.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.eventDetails.QRCodeFragment;
import com.example.beethere.ui.myEvents.ConfirmDeleteFragment;
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

        TextView userName = view.findViewById(R.id.user_name);
        TextView userType = view.findViewById(R.id.user_type);
        ImageButton optionsMenuButton = view.findViewById(R.id.menu_button);
        userName.setText(user.getName());

        if (Boolean.TRUE.equals(user.getOrganizer()) && !Boolean.TRUE.equals(user.getAdmin())) {
            userType.setText("Organizer");
            userType.setSelected(true);
        } else if (!Boolean.TRUE.equals(user.getOrganizer()) && !Boolean.TRUE.equals(user.getAdmin())) {
            userType.setText("Entrant");
            userType.setSelected(false);
        }


        final User currentUser = getItem(position);

        optionsMenuButton.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(getContext(), R.style.CustomPopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.getMenuInflater().inflate(R.menu.admin_profiles_options, popupMenu.getMenu());

            MenuItem reportOrganizerItem = popupMenu.getMenu().findItem(R.id.remove_organizer);
            reportOrganizerItem.setVisible(Boolean.TRUE.equals(currentUser.getOrganizer()));

            MenuItem deleteProfileItem = popupMenu.getMenu().findItem(R.id.delete_profile);
            SpannableString redText = new SpannableString(deleteProfileItem.getTitle());
            redText.setSpan(new ForegroundColorSpan(Color.RED), 0, redText.length(), 0);
            deleteProfileItem.setTitle(redText);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();

                if (id == R.id.delete_profile) {
                    DatabaseFunctions dbFunctions = new DatabaseFunctions();
                    dbFunctions.deleteUserDB(currentUser);
                    users.remove(position);
                    notifyDataSetChanged();
                    showSnackbar(v, "User deleted");
                    return true;
                }

                if (id == R.id.remove_organizer) {
                    currentUser.setViolation(Boolean.TRUE);
                    notifyDataSetChanged();
                    showSnackbar(v, "Organizer reported");
                    return true;
                }

                return false;
            });

            popupMenu.show();
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
