package com.example.beethere.ui.myEvents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.adapters.MyEventsAdapter;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.device.DeviceIDViewModel;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.beethere.DatabaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageKt;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.app.DatePickerDialog;
import android.widget.Toast;
import java.util.Calendar;
import java.util.UUID;


public class CreateEventFragment1 extends Fragment {

    private ImageView eventPoster;
    public Uri imageURL;

    private DeviceIDViewModel deviceIDViewModel;
    private User organizer;

    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://beethere-images");
    private StorageReference storageReference;

    private DatabaseFunctions dbFunctions = new DatabaseFunctions();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


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
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_pg1, container, false);

        storageReference = storage.getReference();

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageURL = uri;
                        try {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                            if (inputStream != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                eventPoster.setImageBitmap(bitmap);
                                eventPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                errorMessage.setText("Unable to open selected image.");
                                errorMessage.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            errorMessage.setText("Error uploading image: " + e.getMessage());
                            errorMessage.setVisibility(View.VISIBLE);
                        }
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
                .addOnSuccessListener(snap -> organizer = snap.toObject(User.class));

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

        eventPoster.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        View.OnClickListener dateClickListener = v -> showDatePicker((EditText) v);
        regStart.setOnClickListener(dateClickListener);
        regEnd.setOnClickListener(dateClickListener);
        eventStart.setOnClickListener(dateClickListener);
        eventEnd.setOnClickListener(dateClickListener);

        timeStart.setOnClickListener(v -> {
            TimePickerFragment dialog = new TimePickerFragment(timeStart);
            dialog.show(getParentFragmentManager(), "timePickerStart");
        });

        timeEnd.setOnClickListener(v -> {
            TimePickerFragment dialog = new TimePickerFragment(timeEnd);
            dialog.show(getParentFragmentManager(), "timePickerEnd");
        });

        maxWaitList.setVisibility(maxWaitListSwitch.isChecked() ? View.VISIBLE : View.GONE);

        maxWaitListSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            maxWaitList.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            wantMaxWaitList = isChecked;
        });

        geoLocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> wantGeoLocation = isChecked);
        randomSelectSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> wantRandomSelect = isChecked);

        AppCompatButton completeButton = view.findViewById(R.id.completeButton);
        completeButton.setOnClickListener(v -> complete());

        AppCompatImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> showGoBackDialog(view));

        return view;
    }

    private void showDatePicker(EditText targetEditText) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    targetEditText.setText(date);
                }, year, month, day);
        dialog.show();
    }

    private void backMain(View view) {
        NavController nav = Navigation.findNavController(view);
        nav.navigate(R.id.createEventstoMyEvents);
    }

    private void showGoBackDialog(View view) {
        GoBackDialogFragment dialog = new GoBackDialogFragment();
        dialog.setGoBackListener(() -> backMain(view));
        dialog.show(getParentFragmentManager(), "GoBackDialog");
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
            errorMessage.setText("Please fill all required fields.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (descInput.length() > 500) {
            errorMessage.setText("Description cannot be longer than 500 characters.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (titleInput.length() > 24) {
            errorMessage.setText("Title cannot be longer than 24 characters.");
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
            int maxWaitListInt = wantMaxWaitList && !maxWaitListInput.isEmpty() ? Integer.parseInt(maxWaitListInput) : 0;

            if (regStartDate.isAfter(regEndDate) || eventStartDate.isAfter(eventEndDate)) {
                errorMessage.setText("Ensure start date is before end date.");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            if (startTime.isAfter(endTime)) {
                errorMessage.setText("Ensure start time is before end time.");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }


            StorageReference ref = storageReference.child("images/" + UUID.randomUUID() + ".jpg");

            ref.putFile(imageURL)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            addToDatabase(titleInput, regStartInput, regEndInput, eventStartInput, eventEndInput,
                                    timeStartInput, timeEndInput, maxAttendeesInt, descInput, downloadUrl,
                                    wantMaxWaitList, wantGeoLocation, wantRandomSelect, maxWaitListInt);

                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.popBackStack();
                            Toast.makeText(getActivity(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                            organizer.setOrganizer(true);
                        });
                        }).addOnFailureListener(e -> {
                            errorMessage.setText("Upload Error: " + e);
                            errorMessage.setVisibility(View.VISIBLE);
                        });

        } catch (Exception e) {
            Log.e("CreateEventFragment", "Parsing error: " + e.getMessage());
            errorMessage.setText("Ensure input formats are correct.");
            errorMessage.setVisibility(View.VISIBLE);
        }

    }

    private void addToDatabase(String title, String regStart, String regEnd, String eventStart,
                               String eventEnd, String timeStart, String timeEnd, int maxAttendees,
                               String description, String posterPath, boolean wantMaxWaitList, boolean wantGeoLocation,
                               boolean wantRandomSelect, int maxWaitListInt) {

        DocumentReference newEventRef = db.collection("events").document();
        String eventID = newEventRef.getId();
        boolean status = true;
        ArrayList<User> waitList = new ArrayList<>();
        Map<String, Boolean> invited = new HashMap<>();
        ArrayList<User> registered = new ArrayList<>();

        Event event;
        if (wantMaxWaitList) {
            event = new Event(organizer, eventID, title, description, posterPath,
                    status, regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
                    maxAttendees, wantGeoLocation, waitList, invited,
                    registered, wantRandomSelect, maxWaitListInt);
        } else {
            event = new Event(organizer, eventID, title, description, posterPath,
                    status, regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
                    maxAttendees, wantGeoLocation, waitList, invited,
                    registered, wantRandomSelect);
        }

        dbFunctions.addEventDB(event);
        events.add(event);
        myEventsAdapter.notifyDataSetChanged();
    }
}