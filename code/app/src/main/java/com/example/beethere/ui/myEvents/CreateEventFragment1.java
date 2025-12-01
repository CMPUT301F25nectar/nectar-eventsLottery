package com.example.beethere.ui.myEvents;

import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beethere.adapters.MyEventsAdapter;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.device.DeviceIDViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.beethere.DatabaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
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

    private ActivityResultLauncher<String> pickImageLauncher;

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */

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
                                showErrorMessage("Unable to open selected image.");
                            }
                        } catch (Exception e) {
                            showErrorMessage("Error uploading image: " + e.getMessage());
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


    /**
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (bottomNav.getSelectedItemId() == itemId) return true;

            NavController navController = NavHostFragment.findNavController(this);
            int currentDestination = navController.getCurrentDestination().getId();

            if (currentDestination == R.id.navigation_createEvents) {
                GoBackDialogFragment dialog = new GoBackDialogFragment();
                dialog.setGoBackListener(() -> {
                    navigateTo(itemId);
                    bottomNav.setSelectedItemId(itemId);
                });
                dialog.show(getParentFragmentManager(), "GoBackDialog");
                return false;
            } else {
                navigateTo(itemId);
                bottomNav.setSelectedItemId(itemId);
                return true;
            }
        });
    }

    /**
     *
     * @param targetEditText shows datePicker selector based on editText target clicked
     */

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


    /**
     *
     * @param view takes in view for navigation from current view
     */
    private void backMain(View view) {
        NavController nav = Navigation.findNavController(view);
        nav.navigate(R.id.createEventstoMyEvents);
    }

    /**
     *
     * @param view takes in view to move back to after option selected
     */
    private void showGoBackDialog(View view) {
        GoBackDialogFragment dialog = new GoBackDialogFragment();
        dialog.setGoBackListener(() -> backMain(view));
        dialog.show(getParentFragmentManager(), "GoBackDialog");
    }

    /**
     *
     * @param itemId takes in itemID if user switches through bottom navigation
     */
    private void navigateTo(final int itemId) {
        NavController nav = NavHostFragment.findNavController(this);
        if (itemId == R.id.navigation_events) {
            nav.navigate(R.id.navigation_events);
        } else if (itemId == R.id.navigation_joined) {
            nav.navigate(R.id.navigation_joined);
        } else if (itemId == R.id.navigation_myEvents) {
            nav.navigate(R.id.navigation_myEvents);
        } else if (itemId == R.id.navigation_notifications) {
            nav.navigate(R.id.navigation_notifications);
        } else if (itemId == R.id.navigation_profile) {
            nav.navigate(R.id.navigation_profile);
        }
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
            showErrorMessage("Please fill all required fields.");
            return;
        }


        if (descInput.length() > 500) {
            showErrorMessage("Description cannot be longer than 500 characters.");
            return;
        }

        if (titleInput.length() > 24) {
            showErrorMessage("Title cannot be longer than 24 characters.");
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

            if (wantMaxWaitList) {
                if (maxWaitListInt < maxAttendeesInt) {
                    showErrorMessage("Number of wait-list entrants must exceed max attendees.");
                    return;
                }
            }

            if (regStartDate.isAfter(regEndDate) || eventStartDate.isAfter(eventEndDate)) {
                showErrorMessage("Ensure start date is before end date.");
                return;
            }

            if (startTime.isAfter(endTime)) {
                showErrorMessage("Ensure start time is before end time.");
                return;
            }

            if (regEndDate == eventStartDate || regEndDate.isAfter(eventStartDate)) {
                showErrorMessage("Registration and event dates cannot overlap");
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
                            showErrorMessage("Upload Error: " + e);
                        });

        } catch (Exception e) {
            showErrorMessage("Ensure input formats are correct.");
        }

    }

    /**
     *
     * @param title title of event
     * @param regStart registration begin date
     * @param regEnd registration end date
     * @param eventStart event start date
     * @param eventEnd event end date
     * @param timeStart event time start
     * @param timeEnd event end start
     * @param maxAttendees max number of people who can attend event
     * @param description description of the event
     * @param posterPath path of the poster image
     * @param wantMaxWaitList boolean value, checking if they'd like a max
     *                       number of wait-list entrants
     * @param wantGeoLocation boolean value, checking if they'd like the geolocation of entrants
     * @param wantRandomSelect boolean value, checking if they'd like to randomly
     *                         select from wait-list if invitees decline/are deleted
     * @param maxWaitListInt mac number of people in a waitlist
     */
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

    /**
     *
     * @param message message to display at the bottom of view
     */
    private void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

}