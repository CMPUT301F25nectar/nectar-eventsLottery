package com.example.beethere.ui.myEvents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.example.beethere.User;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateEventFragment1 extends Fragment {

    private ImageView eventPoster;
    private Uri imageURL;


    private TextInputLayout eventTitle, regStart, regEnd, eventStart,
            eventEnd, timeStart, timeEnd, maxAttend, eventDesc;
    private TextView errorMessage;

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    // Launcher for selecting image
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_pg1, container, false);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageURL = result.getData().getData();
                        eventPoster.setImageURI(imageURL);
                    }
                }
        );

        eventPoster = view.findViewById(R.id.poster);
        eventTitle = view.findViewById(R.id.eventTitle);
        regStart = view.findViewById(R.id.regStart);
        regEnd = view.findViewById(R.id.regEnd);
        eventStart = view.findViewById(R.id.eventStart);
        eventEnd = view.findViewById(R.id.eventEnd);
        timeStart = view.findViewById(R.id.timeStart);
        timeEnd = view.findViewById(R.id.timeEnd);
        maxAttend = view.findViewById(R.id.maxAttend);
        eventDesc = view.findViewById(R.id.description);
        errorMessage = view.findViewById(R.id.errorMessage);
        eventPoster.setOnClickListener(v -> choosePoster());

        Button completeButton = view.findViewById(R.id.completeButton);
        completeButton.setOnClickListener(v -> complete());

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> backMain());

        return view;
    }

    private void backMain () {
        //TODO: display pop up asking if they are sure they'd like to go back
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }


    private void choosePoster() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    @SuppressLint("SetTextI18n")
    private void complete() {
        String titleInput = eventTitle.getEditText().getText().toString().trim();
        String regStartInput = regStart.getEditText().getText().toString().trim();
        String regEndInput = regEnd.getEditText().getText().toString().trim();
        String eventStartInput = eventStart.getEditText().getText().toString().trim();
        String eventEndInput = eventEnd.getEditText().getText().toString().trim();
        String timeStartInput = timeStart.getEditText().getText().toString().trim();
        String timeEndInput = timeEnd.getEditText().getText().toString().trim();
        String maxAttendInput = maxAttend.getEditText().getText().toString().trim();
        String descInput = eventDesc.getEditText().getText().toString().trim();

        // Validation
        if (titleInput.isEmpty() || regStartInput.isEmpty() || regEndInput.isEmpty() ||
                eventStartInput.isEmpty() || eventEndInput.isEmpty() ||
                timeStartInput.isEmpty() || timeEndInput.isEmpty() ||
                maxAttendInput.isEmpty() || descInput.isEmpty() || imageURL == null) {
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (descInput.length() > 500) {
            errorMessage.setText("Description cannot be longer than 500 characters.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }
        try {
            Date regStartDate = dateFormatter.parse(regStartInput);
            Date regEndDate = dateFormatter.parse(regEndInput);
            Date eventStartDate = dateFormatter.parse(eventStartInput);
            Date eventEndDate = dateFormatter.parse(eventEndInput);
            Date startTime = timeFormatter.parse(timeStartInput);
            Date endTime = timeFormatter.parse(timeEndInput);
            int maxAttendeesInt = Integer.parseInt(maxAttendInput);

            if (regStartDate.after(regEndDate) || eventStartDate.after(eventEndDate) || startTime.after(endTime)) {//i dont event think this works because im using date instead of local date
                errorMessage.setText("Ensure start dates/times are before end dates/times.");
                errorMessage.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            errorMessage.setText("Ensure input formats are correct.");
            errorMessage.setVisibility(View.VISIBLE);
        }

        //TODO: Add event to database once all tests are passed
        // if the switches are true, apply seperate field
    }
}

