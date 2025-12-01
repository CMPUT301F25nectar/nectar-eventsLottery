package com.example.beethere.ui.adminDashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beethere.R;

public class AdminDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        Button buttonBack = view.findViewById(R.id.admin_back_button);
        LinearLayout viewNotification = view.findViewById(R.id.btn_view_notifications);
        LinearLayout viewImages = view.findViewById(R.id.btn_view_images);
        LinearLayout viewProfiles = view.findViewById(R.id.btn_view_profiles);
        LinearLayout viewEvents = view.findViewById(R.id.btn_view_events);

        buttonBack.setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        viewNotification.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminDashboardFragment.this)
                        .navigate(R.id.admin_to_adminNotifs)
        );

        viewImages.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminDashboardFragment.this)
                        .navigate(R.id.admin_to_adminImages)
        );

        viewProfiles.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminDashboardFragment.this)
                        .navigate(R.id.admin_to_adminProfiles)
        );

        viewEvents.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminDashboardFragment.this)
                        .navigate(R.id.admin_to_adminEvents)
        );

        return view;
    }
}
