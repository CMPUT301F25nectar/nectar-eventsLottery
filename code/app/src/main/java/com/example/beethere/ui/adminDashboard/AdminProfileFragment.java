package com.example.beethere.ui.adminDashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.adapters.AdminProfilesAdapter;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.ui.profile.NotificationSettingsFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminProfileFragment extends Fragment {

    private ListView allProfilesListView;
    private ImageButton backButton, filter;
    private SearchView searchBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> usersList = new ArrayList<>();
    private ArrayList<User> displayedList = new ArrayList<>();
    private AdminProfilesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_view_profiles, container, false);

        allProfilesListView = view.findViewById(R.id.profiles_list_view);
        backButton = view.findViewById(R.id.back_button);
        searchBar = view.findViewById(R.id.admin_profile_search);
        filter = view.findViewById(R.id.button_filter);


        db = FirebaseFirestore.getInstance();
        adapter = new AdminProfilesAdapter(requireContext(), displayedList);
        allProfilesListView.setAdapter(adapter);


        loadUsers();


        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return false;
            }
        });

        filter.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(getContext(), R.style.CustomPopupMenu);
            PopupMenu popup = new PopupMenu(wrapper, filter);

            Menu menu = popup.getMenu();
            MenuItem showAll = menu.add("Show All");
            MenuItem organizers = menu.add("Organizers");
            MenuItem entrants = menu.add("Entrants");

            // Apply colors
            organizers.setTitle(colored("Organizers", "#AB6CB9")); // light purple
            entrants.setTitle(colored("Entrants", "#528AAE"));     // light blue


            popup.setOnMenuItemClickListener(item -> {
                switch (item.getTitle().toString()) {
                    case "Organizers":
                        filterUsers(Boolean.TRUE);
                        break;
                    case "Entrants":
                        filterUsers(Boolean.FALSE);
                        break;
                    default:
                        displayedList.clear();
                        displayedList.addAll(usersList);
                        adapter.notifyDataSetChanged();
                }
                return true;
            });
            popup.show();
        });


        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });


        return view;
    }

    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(query -> {
                    usersList.clear();
                    for (DocumentSnapshot doc : query) {
                        User user = doc.toObject(User.class);
                        if (user != null && !Boolean.TRUE.equals(user.getAdmin())) {
                            user.setDeviceid(doc.getId());
                            usersList.add(user);
                            displayedList.clear();
                            displayedList.addAll(usersList);
                            adapter.notifyDataSetChanged();

                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show()
                );
    }
    private void searchUsers(String query) {
        String lowerQuery = query.toLowerCase().trim();

        ArrayList<User> filteredSource = new ArrayList<>(usersList);
        displayedList.clear();

        if (lowerQuery.isEmpty()) {
            displayedList.addAll(filteredSource);
        } else {
            for (User user : filteredSource) {
                if (user.getName().toLowerCase().contains(lowerQuery)) {
                    displayedList.add(user);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }


    private void filterUsers(Boolean status) {
        displayedList.clear();

        for (User user : usersList) {
            if (Boolean.TRUE.equals(user.getOrganizer()) == status) {
                displayedList.add(user);
            }
        }

        adapter.notifyDataSetChanged();
    }
    private SpannableString colored(String text, String colorHex) {
        SpannableString span = new SpannableString(text);
        span.setSpan(new ForegroundColorSpan(Color.parseColor(colorHex)),
                0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

}
