package com.example.beethere.ui.myEvents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.adapters.MyEventsAdapter;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditingEventFragment extends Fragment implements NavigationBarView.OnItemSelectedListener {

    private ImageView eventPoster;
    public Uri imageURL;

    DatabaseFunctions dbFunctions = new DatabaseFunctions();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText eventTitle, regStart, regEnd, eventStart,
            eventEnd, timeStart, timeEnd, eventDesc;
    private AppCompatButton deleteEventButton;

    private Uri posterUri;
    private TextView errorMessage;
    private MyEventsAdapter myEventsAdapter;

    private String eventTitleInput, regStartInput, regEndInput, eventStartInput,
            eventEndInput, timeStartInput, timeEndInput, eventDescInput, posterInput;

    LocalDate currentDate = LocalDate.now();

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private ArrayList<Event> events;
    private static final String ARG_EVENT_ID = "eventID";

    private Event event;
    private String eventID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editing_event, container, false);

        eventPoster = view.findViewById(R.id.poster);
        eventTitle = view.findViewById(R.id.eventTitle);
        regStart = view.findViewById(R.id.regStart);
        regEnd = view.findViewById(R.id.regEnd);
        eventStart = view.findViewById(R.id.eventStart);
        eventEnd = view.findViewById(R.id.eventEnd);
        timeStart = view.findViewById(R.id.timeStart);
        timeEnd = view.findViewById(R.id.timeEnd);
        eventDesc = view.findViewById(R.id.description);
        errorMessage = view.findViewById(R.id.errorMessage);
        deleteEventButton = view.findViewById(R.id.deleteEvent);
        

        db.collection("events")
                .document(eventID)
                .get()
                .addOnSuccessListener(snapshot -> {
                    event = snapshot.toObject(Event.class);
                    if (event == null) return;

                    eventTitle.setText(event.getTitle());
                    eventDesc.setText(event.getDescription());
                    regStart.setText(event.getRegStart());
                    regEnd.setText(event.getRegEnd());
                    eventStart.setText(event.getEventDateStart());
                    eventEnd.setText(event.getEventDateEnd());
                    timeStart.setText(event.getEventTimeStart());
                    timeEnd.setText(event.getEventTimeEnd());

                    if (event.getPosterPath() != null) {
                        posterUri = Uri.parse(event.getPosterPath());
                        eventPoster.setImageURI(posterUri);
                        eventPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    if (!regStart.getText().toString().isEmpty() && !regEnd.getText().toString().isEmpty()) {
                        LocalDate regStartDate = LocalDate.parse(regStart.getText().toString(), dateFormatter);
                        LocalDate regEndDate = LocalDate.parse(regEnd.getText().toString(), dateFormatter);

                        if (!currentDate.isBefore(regStartDate)) {
                            regStart.setEnabled(false);
                            regStart.setBackgroundColor(Color.LTGRAY); //TODO this needs fixing
                        } else {
                            regStart.setOnClickListener(v -> showDatePicker(regStart));
                        }
                        if (!currentDate.isBefore(regEndDate)) {
                            regEnd.setEnabled(false);
                            regEnd.setBackgroundColor(Color.LTGRAY);  //TODO this needs fixing
                        } else {
                            regEnd.setOnClickListener(v -> showDatePicker(regEnd));
                        }
                    }

                    eventStart.setOnClickListener(v -> showDatePicker(eventStart));
                    eventEnd.setOnClickListener(v -> showDatePicker(eventEnd));
                });

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);

        BottomNavigationView.OnItemSelectedListener listener;

        listener = item -> {
            int itemId = item.getItemId();

            GoBackDialogFragment dialog = new GoBackDialogFragment();
            dialog.setGoBackListener(() -> {
                NavController nav = Navigation.findNavController(requireView());
                if (itemId == R.id.navigation_events) {
                    nav.navigate(R.id.EditEventsToAllEvents);
                } else if (itemId == R.id.navigation_joined) {
                    nav.navigate(R.id.EditEventsToJoinedEvents);
                } else if (itemId == R.id.navigation_myEvents) {
                    nav.navigate(R.id.EditEventsToMyEvents);
                } else if (itemId == R.id.navigation_notifications) {
                    nav.navigate(R.id.EditEventsToNotifications);
                } else if (itemId == R.id.navigation_profile) {
                    nav.navigate(R.id.EditEventsToProfile);
                }
            });

            dialog.show(getParentFragmentManager(), "GoBackDialog");

            return false;
        };

        bottomNav.setOnItemSelectedListener(listener);


        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();

                        try {
                            requireContext().getContentResolver().takePersistableUriPermission(
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }

                        imageURL = uri;
                        eventPoster.setImageURI(imageURL);

                        eventPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
        );

        eventPoster.setOnClickListener(v -> choosePoster());
        View.OnClickListener dateClickListener = v -> showDatePicker((EditText) v);

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

        AppCompatButton completeButton = view.findViewById(R.id.completeButton);
        completeButton.setOnClickListener(v -> complete(view));

        AppCompatImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> showGoBackDialog(view));
        
        deleteEventButton.setOnClickListener(v -> showDeleteDialog());


        return view;
    }


    private void choosePoster() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImageLauncher.launch(intent);
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
        nav.navigate(R.id.EditEventsToMyEvents);
    }

    private void showGoBackDialog(View view) {
        GoBackDialogFragment dialog = new GoBackDialogFragment();
        dialog.setGoBackListener(() -> backMain(view));
        dialog.show(getParentFragmentManager(), "GoBackDialog");
    }

    @SuppressLint("SetTextI18n")
    public void complete(View view) {
        String newTitle = eventTitle.getText().toString().trim();
        String newRegStart = regStart.getText().toString().trim();
        String newRegEnd = regEnd.getText().toString().trim();
        String newStartDate = eventStart.getText().toString().trim();
        String newEndDate = eventEnd.getText().toString().trim();
        String newTimeStart = timeStart.getText().toString().trim();
        String newTimeEnd = timeEnd.getText().toString().trim();
        String newDesc = eventDesc.getText().toString().trim();

        // Validation
        if (newTitle.isEmpty() || newRegStart.isEmpty() || newRegEnd.isEmpty() ||
                newStartDate.isEmpty() || newEndDate.isEmpty() ||
                newTimeStart.isEmpty() || newTimeEnd.isEmpty()
                || newDesc.isEmpty() || (imageURL == null && posterUri == null)) {
            errorMessage.setText("Please fill all required fields.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (newDesc.length() > 500) {
            errorMessage.setText("Description cannot be longer than 500 characters.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (newTitle.length() > 24) {
            errorMessage.setText("Title cannot be longer than 24 characters.");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        try {
            LocalDate regStartDate = LocalDate.parse(newRegStart.trim(), dateFormatter);
            LocalDate regEndDate = LocalDate.parse(newRegEnd.trim(), dateFormatter);
            LocalDate eventStartDate = LocalDate.parse(newStartDate.trim(), dateFormatter);
            LocalDate eventEndDate = LocalDate.parse(newEndDate.trim(), dateFormatter);

            LocalTime startTime = LocalTime.parse(newTimeStart.trim(), timeFormatter);
            LocalTime endTime = LocalTime.parse(newTimeEnd.trim(), timeFormatter);


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

            updateEvent(newTitle, newRegStart, newRegEnd, newStartDate, newEndDate,
                    newTimeStart, newTimeEnd, newDesc,
                    imageURL != null ? imageURL.toString() : posterUri.toString());

            backMain(view);
            Toast.makeText(getActivity(), "Event updated successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("CreateEventFragment", "Parsing error: " + e.getMessage());
            errorMessage.setText("Ensure input formats are correct.");
            errorMessage.setVisibility(View.VISIBLE);
        }

    }

    private void updateEvent(String titleInput, String regStartInput, String regEndInput,
                             String eventStartInput, String eventEndInput, String timeStartInput,
                             String timeEndInput, String descInput, String imageInput) {
        event.setTitle(titleInput);
        event.setRegStart(regStartInput);
        event.setRegEnd(regEndInput);
        event.setEventDateStart(eventStartInput);
        event.setEventDateEnd(eventEndInput);
        event.setEventTimeStart(timeStartInput);
        event.setEventTimeEnd(timeEndInput);
        event.setDescription(descInput);
        event.setPosterPath(imageInput);

        updateDB();
    }

    public void updateDB(){
        db.collection("events")
                .document(eventID)
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EditingEvent", "Event updated successfully in Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("EditingEvent", "Failed to update: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to update event.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteDialog() {
        ConfirmDeleteFragment dialog = new ConfirmDeleteFragment(eventID);

        dialog.setOnEventDeletedListener(id -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.EditEventsToMyEvents);
        });

        dialog.show(getParentFragmentManager(), "ConfirmDelete");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}



