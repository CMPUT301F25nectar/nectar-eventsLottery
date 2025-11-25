package com.example.beethere.ui.myEvents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.beethere.device.DeviceId;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.device.DeviceIDViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.example.beethere.DatabaseFunctions;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CreateEventFragment1 extends Fragment {

    private ImageView eventPoster;
    public Uri imageURL;

    private DeviceIDViewModel deviceIDViewModel;
    private User organizer;

    DatabaseFunctions dbFunctions = new DatabaseFunctions();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean wantMaxWaitList, wantRandomSelect, wantGeoLocation = false;

    private EditText eventTitle, regStart, regEnd, eventStart,
            eventEnd, timeStart, timeEnd, maxAttend, eventDesc, maxWaitList;
    private SwitchCompat maxWaitListSwitch, randomSelectSwitch, geoLocationSwitch;
    public TextView errorMessage;
    private MyEventsAdapter myEventsAdapter;
    public ArrayList<Event> events;


    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

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

        events = new ArrayList<>();
        myEventsAdapter = new MyEventsAdapter(getContext(), events);

        deviceIDViewModel = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
        String deviceID = deviceIDViewModel.getDeviceID();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID)
                .get()
                .addOnSuccessListener(snap -> {
                    organizer = snap.toObject(User.class);
                });


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

        maxWaitList.setVisibility(maxWaitListSwitch.isChecked() ? View.VISIBLE : View.GONE);

        // Add listeners to handle switch state changes
        maxWaitListSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                maxWaitList.setVisibility(View.VISIBLE);  // Show maxWaitList input
                wantMaxWaitList = isChecked;
            } else {
                maxWaitList.setVisibility(View.GONE);  // Hide maxWaitList input
            }
        });

        // GeoLocation Switch listener
        geoLocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            wantGeoLocation = isChecked;  // Set the value of wantGeoLocation based on the switch state
        });

        // Random Select Switch listener
        randomSelectSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            wantRandomSelect = isChecked;  // Set the value of wantRandomSelect based on the switch state
        });

        Button completeButton = view.findViewById(R.id.completeButton);
        completeButton.setOnClickListener(v -> complete());

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> backMain());


        return view;
    }

    private void backMain () {
//        DialogFragment goBack
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    private void choosePoster() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickImageLauncher.launch(intent);
    }

    @SuppressLint("SetTextI18n")
    public void complete() {
        String titleInput = eventTitle.getText().toString().trim();
        String regStartInput = regStart.getText().toString().trim();
        String regEndInput = regEnd.getText().toString().trim();
        String eventStartInput = eventStart.getText().toString().trim();
        String eventEndInput = eventEnd.getText().toString().trim();
        String timeStartInput = timeStart.getText().toString().trim();
        String timeEndInput = timeEnd.getText().toString().trim();
        String maxAttendInput = maxAttend.getText().toString().trim();
        String descInput = eventDesc.getText().toString().trim();
        String maxWaitListInput = maxWaitList.getText().toString().trim();

        // Validation
        if (titleInput.isEmpty() || regStartInput.isEmpty() || regEndInput.isEmpty() ||
                eventStartInput.isEmpty() || eventEndInput.isEmpty() ||
                timeStartInput.isEmpty() || timeEndInput.isEmpty() ||
                maxAttendInput.isEmpty() || descInput.isEmpty() ||
                (wantMaxWaitList && maxWaitListInput.isEmpty()) || imageURL == null) {
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (descInput.length() > 500) {
            errorMessage.setText("Description cannot be longer than 500 characters.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }
        try {
            LocalDate regStartDate = LocalDate.parse(regStartInput, dateFormatter);
            LocalDate regEndDate = LocalDate.parse(regEndInput, dateFormatter);
            LocalDate eventStartDate = LocalDate.parse(eventStartInput, dateFormatter);
            LocalDate eventEndDate = LocalDate.parse(eventEndInput, dateFormatter);

            LocalTime startTime = LocalTime.parse(timeStartInput, timeFormatter);
            LocalTime endTime = LocalTime.parse(timeEndInput, timeFormatter);
            int maxAttendeesInt = Integer.parseInt(maxAttendInput);
            int maxWaitListInt = 0;

            if (wantMaxWaitList && !maxWaitListInput.isEmpty()) {
                maxWaitListInt = Integer.parseInt(maxWaitListInput);
            }

            if (regStartDate.isAfter(regEndDate) || eventStartDate.isAfter(eventEndDate) || startTime.isAfter(endTime)) {
                errorMessage.setVisibility(View.VISIBLE);
            }

            addToDatabase(titleInput, regStartInput, regEndInput, eventStartInput, eventEndInput,
                    timeStartInput, timeEndInput, maxAttendeesInt, descInput, imageURL.toString(),
                    wantMaxWaitList, wantGeoLocation, wantRandomSelect, maxWaitListInt);

            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
            android.widget.Toast.makeText(getActivity(), "Event created successfully!", android.widget.Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("CreateEventFragment", "Parsing error: " + e.getMessage());//checker
            errorMessage.setText("Ensure input formats are correct.");
            errorMessage.setVisibility(View.VISIBLE);
        }

    }

    private void addToDatabase(String title, String regStart, String regEnd, String eventStart,
                               String eventEnd, String timeStart, String timeEnd, int maxAttendees,
                               String description, String posterPath, boolean wantMaxWaitList, boolean wantGeoLocation,
                               boolean wantRandomSelect, int maxWaitListInt) throws WriterException {

        DocumentReference newEventRef = db.collection("events").document(); // auto-generated ID
        String eventID = newEventRef.getId();

        boolean status = true;

        ArrayList<User> waitList = new ArrayList<>();
        Map<User, Boolean> invited = new HashMap<>();
        ArrayList<User> registered = new ArrayList<>();

        if (wantMaxWaitList) {
            Event event = new Event(organizer, eventID, title, description, posterPath,
                    status, regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
                    maxAttendees, wantGeoLocation, waitList, invited,
                    registered, wantRandomSelect);
            dbFunctions.addEventDB(event);
            events.add(event);
            myEventsAdapter.notifyDataSetChanged();


        } else {
            Event event = new Event(organizer, eventID, title, description, posterPath,
                    status, regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
                    maxAttendees, wantGeoLocation,  waitList, invited,
                    registered, wantRandomSelect, maxWaitListInt);

            dbFunctions.addEventDB(event);
            events.add(event);
            myEventsAdapter.notifyDataSetChanged();
        }
    }
}











