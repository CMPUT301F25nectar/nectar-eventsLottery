package com.example.beethere.ui.adminDashboard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.example.beethere.ui.profile.NotificationSettingsFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminProfileFragment extends Fragment {

    private ListView allProfilesListView;
    private ImageButton backButton;
    private SearchView searchBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> usersList;
    private AdminProfilesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_view_profiles, container, false);

        allProfilesListView = view.findViewById(R.id.profiles_list_view);
        backButton = view.findViewById(R.id.back_button);
        searchBar = view.findViewById(R.id.admin_profile_search);

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


































//    private ListView profileListView;
//    private EditText searchBar;
//    private ImageButton backButton;
//    private ProfileAdapter adapter;
//    private List<User> allUsers;
//    private List<User> filteredUsers;
//    private DatabaseFunctions dbFunctions;
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_admin_view_profiles, container, false);
//        dbFunctions = new DatabaseFunctions();
//
//        profileListView = view.findViewById(R.id.profiles_list_view);
//        searchBar = view.findViewById(R.id.search_bar);
//        backButton = view.findViewById(R.id.back_button);
//
//        allUsers = new ArrayList<>();
//        filteredUsers = new ArrayList<>();
//
//        adapter = new ProfileAdapter();
//        profileListView.setAdapter(adapter);
//        backButton.setOnClickListener(v ->
//                NavHostFragment.findNavController(AdminProfileFragment.this).navigateUp()
//        );
//        searchBar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count){
//                filterUsers(s.toString());
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//
//        });
//        loadUsers();
//        return view;
//    }
//    private void loadUsers(){
//        dbFunctions.getUsersDB(new DatabaseCallback<List<User>>() {
//            @Override
//            public void onCallback(List<User> result) {
//                allUsers.clear();
//                allUsers.addAll(result);
//                filteredUsers.clear();
//                filteredUsers.addAll(allUsers);
//                adapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//    }
//    private void filterUsers(String query) {
//        filteredUsers.clear();
//
//        if(query.isEmpty()){
//            filteredUsers.addAll(allUsers);
//        } else{
//            String lowerQuery = query.toLowerCase();
//            for (User user : allUsers) {
//                if (user.getName() != null && user.getName().toLowerCase().contains(lowerQuery) ||
//                user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery)) {
//                    filteredUsers.add(user);
//                }
//
//            }
//        }
//        adapter.notifyDataSetChanged();
//    }
//    private void showDeleteConfirmationDialog(User user) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setTitle("Delete User");
//
//        builder.setMessage("Are you sure you want to delete" + user.getName() +"?\n\n");
//
//        builder.setPositiveButton("Delete", (dialog, which) -> {
//            dbFunctions.deleteUserDB(user);
//            allUsers.remove(user);
//            filteredUsers.remove(user);
//            adapter.notifyDataSetChanged();
//        });
//        builder.setNegativeButton("Cancel", null);
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFD32F2F);
//
//    }
//
//    private class ProfileAdapter extends BaseAdapter{
//        @Override
//        public int getCount() {
//            return filteredUsers.size();
//        }
//        @Override
//        public User getItem(int position) {
//            return filteredUsers.get(position);
//        }
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView != null) {
//                convertView = LayoutInflater.from(requireContext())
//                        .inflate(R.layout.fragment_admin_profile_item, parent, false);
//            }
//            User user = getItem(position);
//            TextView userName = convertView.findViewById(R.id.user_name);
//            ImageButton menuButton = convertView.findViewById(R.id.menu_button);
//
//            userName.setText(user.getName() != null ? user.getName() : "user not found");
//
//            menuButton.setOnClickListener(v ->{
//                PopupMenu popup = new PopupMenu(requireContext(), v);
//                popup.getMenu().add("Remove Profile");
//                popup.setOnMenuItemClickListener(item -> {
//                    showDeleteConfirmationDialog(user);
//                    return true;
//                });
//                popup.show();
//            });
//
//            return convertView;
//        }
//    }
}
