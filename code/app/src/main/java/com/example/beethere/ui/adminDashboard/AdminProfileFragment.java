package com.example.beethere.ui.adminDashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.adapters.AdminProfilesAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminProfileFragment extends Fragment {

    private ListView allProfilesListView;
    private ImageButton backButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> usersList;
    private AdminProfilesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_view_profiles, container, false);

        allProfilesListView = view.findViewById(R.id.allProfiles);
        backButton = view.findViewById(R.id.backButton);

        db = FirebaseFirestore.getInstance();
        usersList = new ArrayList<>();

        adapter = new AdminProfilesAdapter(requireContext(), usersList);
        allProfilesListView.setAdapter(adapter);

        loadUsers();

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
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show()
                );
    }
}
