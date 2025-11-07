package com.example.beethere.ui.myEvents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.UserListManager;
import com.google.android.material.textfield.TextInputLayout;
/*import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;*/

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class CreateEventFragment1 extends Fragment {

    private ImageView eventPoster;
    private Uri imageURL;

    private User organizer;

    private boolean wantMaxWaitList, wantRandomSelect, wantGeoLocation = false;

    private TextInputLayout eventTitle, regStart, regEnd, eventStart,
            eventEnd, timeStart, timeEnd, maxAttend, eventDesc, maxWaitList;
    private SwitchCompat maxWaitListSwitch, randomSelectSwitch, geoLocationSwitch;
    private TextView errorMessage;

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");


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
        maxWaitListSwitch = view.findViewById(R.id.maxWaitSwitch);
        randomSelectSwitch = view.findViewById(R.id.randomSelectSwitch);
        geoLocationSwitch = view.findViewById(R.id.geoLocSwitch);
        maxWaitList = view.findViewById(R.id.maxWaitFill);
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
        String maxWaitListInput = maxWaitList.getEditText().getText().toString().trim();

        geoLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    wantGeoLocation = true;
                    maxAttend.setVisibility(View.VISIBLE);
                }
            }
        });

        maxWaitListSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    wantMaxWaitList = true;

                }
            }
        });

        randomSelectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    wantRandomSelect = true;
                }
            }
        });

        // Validation
        if (titleInput.isEmpty() || regStartInput.isEmpty() || regEndInput.isEmpty() ||
                eventStartInput.isEmpty() || eventEndInput.isEmpty() ||
                timeStartInput.isEmpty() || timeEndInput.isEmpty() ||
                maxAttendInput.isEmpty() || descInput.isEmpty() ||
                (wantMaxWaitList && maxWaitListInput.isEmpty()) || imageURL == null) { //just add an asterick by the fill input line
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (descInput.length() > 500) {
            errorMessage.setText("Description cannot be longer than 500 characters.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }
        try {
            LocalDateTime regStartDate = LocalDateTime.parse(regStartInput, dateFormatter);
            LocalDateTime regEndDate = LocalDateTime.parse(regEndInput, dateFormatter);
            LocalDateTime eventStartDate = LocalDateTime.parse(eventStartInput, dateFormatter);
            LocalDateTime eventEndDate = LocalDateTime.parse(eventEndInput, dateFormatter);
            LocalDateTime startTime = LocalDateTime.parse(timeStartInput, timeFormatter);
            LocalDateTime endTime = LocalDateTime.parse(timeEndInput, timeFormatter);
            int maxAttendeesInt = Integer.parseInt(maxAttendInput);
            int maxWaitListInt = Integer.parseInt(maxWaitListInput);

            if (regStartDate.isAfter(regEndDate) || eventStartDate.isAfter(eventEndDate) || startTime.isAfter(endTime)) {
                errorMessage.setText("Ensure start dates/times are before end dates/times.");
                errorMessage.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            errorMessage.setText("Ensure input formats are correct.");
            errorMessage.setVisibility(View.VISIBLE);
        }

        //TODO: Add event to database once all tests are passed, call addToDatebase
    }

    /*private void addToDatabase(String title, LocalDateTime regStart, LocalDateTime regEnd, LocalDateTime eventStart,
                               LocalDateTime eventEnd, LocalDateTime timeStart, LocalDateTime timeEnd, int maxAttendees,
                               String description, String posterPath, boolean wantMaxWaitList, boolean wantGeoLocation,
                               boolean wantRandomSelect, int maxWaitListInt) throws WriterException {

        int eventID;
        //TODO: find the max id in the list of event ids and add 1, default of max is 0
        boolean status = true;

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix QRCode = writer.encode(String.valueOf(eventID), BarcodeFormat.QR_CODE, 300, 300);
        //TODO: add QR code to the collection of QR codes
        if (wantMaxWaitList) {
            UserListManager entrantList = new UserListManager(wantRandomSelect, maxAttendees, maxWaitListInt);

            Event event = new Event(organizer, eventID, title, description, posterPath, QRCode,
                    status, regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
                    maxAttendees, wantGeoLocation, wantRandomSelect);



        } else {
            UserListManager entrantList = new UserListManager(wantRandomSelect, maxAttendees);

            Event event = new Event(organizer, eventID, title, description, posterPath, QRCode,
                    status, regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
                    maxAttendees, wantGeoLocation, wantRandomSelect, maxWaitListInt);
        }

    }*/
}

