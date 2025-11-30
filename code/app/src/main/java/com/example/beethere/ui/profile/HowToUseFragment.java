package com.example.beethere.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beethere.R;
import org.jspecify.annotations.NonNull;

public class HowToUseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_howtouse, container, false);
        ImageButton backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(HowToUseFragment.this)
                        .popBackStack()
        );
        return view;
    }
}
