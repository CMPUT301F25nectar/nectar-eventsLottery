package com.example.beethere.ui.myEvents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.device.DeviceIDViewModel;
import com.example.beethere.eventclasses.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditingEventFragment extends Fragment {

//    private ImageView eventPoster;
//    public Uri imageURL;
//
//    private DeviceIDViewModel deviceIDViewModel;
//    private User organizer;
//    private Event eventToEdit;
//
//    DatabaseFunctions dbFunctions = new DatabaseFunctions();
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    private EditText eventTitle, regStart, regEnd, eventStart,
//            eventEnd, timeStart, timeEnd, maxAttend, eventDesc;
//
//    private Boolean wantMaxWaitList, wantGeoLocation, wantRandomSelect;
//    private int maxWaitList;
//
//    public TextView errorMessage;
//
//    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
//
//    private ActivityResultLauncher<Intent> pickImageLauncher;
//
//    public EditingEventFragment() { }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_create_event_pg1, container, false);
//
//        // Initialize Views
//        eventPoster = view.findViewById(R.id.poster);
//        eventTitle = view.findViewById(R.id.eventTitle);
//        regStart = view.findViewById(R.id.regStart);
//        regEnd = view.findViewById(R.id.regEnd);
//        eventStart = view.findViewById(R.id.eventStart);
//        eventEnd = view.findViewById(R.id.eventEnd);
//        timeStart = view.findViewById(R.id.timeStart);
//        timeEnd = view.findViewById(R.id.timeEnd);
//        maxAttend = view.findViewById(R.id.maxAttend);
//        eventDesc = view.findViewById(R.id.description);
//        maxWaitList = view.findViewById(R.id.maxWaitFill);
//        errorMessage = view.findViewById(R.id.errorMessage);
//
//
//        deviceIDViewModel = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
//        String deviceID = deviceIDViewModel.getDeviceID();
//        db.collection("users").document(deviceID).get()
//                .addOnSuccessListener(snap -> organizer = snap.toObject(User.class));
//
//        pickImageLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri uri = result.getData().getData();
//                        try {
//                            requireContext().getContentResolver().takePersistableUriPermission(
//                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
//                            );
//                        } catch (SecurityException e) { e.printStackTrace(); }
//                        imageURL = uri;
//                        eventPoster.setImageURI(imageURL);
//                    }
//                }
//        );
//
//        eventPoster.setOnClickListener(v -> choosePoster());
//
//        // Prefill if editing
//        if (eventToEdit != null) prefillFields();
//
//        if (eventToEdit.getMaxWaitlist() != null) {
//            wantMaxWaitList = false;
//        }
//
//        wantGeoLocation = eventToEdit.getGeoloc();
//        wantRandomSelect = eventToEdit.getAutoRandomSelection();
//
//        Button completeButton = view.findViewById(R.id.completeButton);
//        completeButton.setOnClickListener(v -> complete());
//
//        Button backButton = view.findViewById(R.id.backButton);
//        backButton.setOnClickListener(v -> showGoBackDialog(view));
//
//        return view;
//    }
//
//    private void prefillFields() {
//        eventTitle.setText(eventToEdit.getTitle());
//        regStart.setText(eventToEdit.getRegStart());
//        regEnd.setText(eventToEdit.getRegEnd());
//        eventStart.setText(eventToEdit.getEventDateStart());
//        eventEnd.setText(eventToEdit.getEventDateEnd());
//        timeStart.setText(eventToEdit.getEventTimeStart());
//        timeEnd.setText(eventToEdit.getEventTimeEnd());
//        maxAttend.setText(String.valueOf(eventToEdit.getEntrantMax()));
//        eventDesc.setText(eventToEdit.getDescription());
//
//        if (eventToEdit.getPosterPath() != null) {
//            imageURL = Uri.parse(eventToEdit.getPosterPath());
//            eventPoster.setImageURI(imageURL);
//        }
//    }
//
//    private void choosePoster() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("image/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        pickImageLauncher.launch(intent);
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void complete() {
//        String titleInput = eventTitle.getText().toString().trim();
//        String regStartInput = regStart.getText().toString().trim();
//        String regEndInput = regEnd.getText().toString().trim();
//        String eventStartInput = eventStart.getText().toString().trim();
//        String eventEndInput = eventEnd.getText().toString().trim();
//        String timeStartInput = timeStart.getText().toString().trim();
//        String timeEndInput = timeEnd.getText().toString().trim();
//        String maxAttendInput = maxAttend.getText().toString().trim();
//        String descInput = eventDesc.getText().toString().trim();
//        String maxWaitListInput = maxWaitList.getText().toString().trim();
//
//        // Validation
//        if (titleInput.isEmpty() || regStartInput.isEmpty() || regEndInput.isEmpty() ||
//                eventStartInput.isEmpty() || eventEndInput.isEmpty() ||
//                timeStartInput.isEmpty() || timeEndInput.isEmpty() ||
//                maxAttendInput.isEmpty() || descInput.isEmpty() ||
//                (wantMaxWaitList && maxWaitListInput.isEmpty()) || imageURL == null) {
//            errorMessage.setVisibility(View.VISIBLE);
//            return;
//        }
//        if (descInput.length() > 500) {
//            errorMessage.setText("Description cannot be longer than 500 characters.");
//            errorMessage.setVisibility(View.VISIBLE);
//            return;
//        }
//
//        try {
//            LocalDate regStartDate = LocalDate.parse(regStartInput, dateFormatter);
//            LocalDate regEndDate = LocalDate.parse(regEndInput, dateFormatter);
//            LocalDate eventStartDate = LocalDate.parse(eventStartInput, dateFormatter);
//            LocalDate eventEndDate = LocalDate.parse(eventEndInput, dateFormatter);
//
//            LocalTime startTime = LocalTime.parse(timeStartInput, timeFormatter);
//            LocalTime endTime = LocalTime.parse(timeEndInput, timeFormatter);
//            int maxAttendeesInt = Integer.parseInt(maxAttendInput);
//            int maxWaitListInt = wantMaxWaitList ? Integer.parseInt(maxWaitListInput) : 0;
//
//            if (regStartDate.isAfter(regEndDate) || eventStartDate.isAfter(eventEndDate) || startTime.isAfter(endTime)) {
//                errorMessage.setVisibility(View.VISIBLE);
//                return;
//            }
//
//            if (eventToEdit != null) {
//                updateEventInDatabase(eventToEdit.getEventID(), titleInput, regStartInput, regEndInput,
//                        eventStartInput, eventEndInput, timeStartInput, timeEndInput,
//                        maxAttendeesInt, descInput, imageURL.toString(), wantMaxWaitList,
//                        wantGeoLocation, wantRandomSelect, maxWaitListInt);
//            } else {
//                addToDatabase(titleInput, regStartInput, regEndInput, eventStartInput, eventEndInput,
//                        timeStartInput, timeEndInput, maxAttendeesInt, descInput, imageURL.toString(),
//                        wantMaxWaitList, wantGeoLocation, wantRandomSelect, maxWaitListInt);
//            }
//
//            getParentFragmentManager().popBackStack();
//        } catch (Exception e) {
//            Log.e("EventFragment", "Parsing error: " + e.getMessage());
//            errorMessage.setText("Ensure input formats are correct.");
//            errorMessage.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void addToDatabase(String title, String regStart, String regEnd, String eventStart,
//                               String eventEnd, String timeStart, String timeEnd, int maxAttendees,
//                               String description, String posterPath, boolean wantMaxWaitList,
//                               boolean wantGeoLocation, boolean wantRandomSelect, int maxWaitListInt) {
//
//        DocumentReference newEventRef = db.collection("events").document();
//        String eventID = newEventRef.getId();
//
//        Event event = new Event(organizer, eventID, title, description, posterPath, true,
//                regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
//                maxAttendees, wantGeoLocation, new ArrayList<>(), new HashMap<>(),
//                new ArrayList<>(), wantRandomSelect, maxWaitListInt);
//
//        dbFunctions.addEventDB(event);
//    }
//
//    private void updateEventInDatabase(String eventID, String title, String regStart, String regEnd,
//                                       String eventStart, String eventEnd, String timeStart,
//                                       String timeEnd, int maxAttendees, String description,
//                                       String posterPath, boolean wantMaxWaitList,
//                                       boolean wantGeoLocation, boolean wantRandomSelect,
//                                       int maxWaitListInt) {
//
//        DocumentReference eventRef = db.collection("events").document(eventID);
//
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("title", title);
//        updates.put("regStart", regStart);
//        updates.put("regEnd", regEnd);
//        updates.put("eventStart", eventStart);
//        updates.put("eventEnd", eventEnd);
//        updates.put("timeStart", timeStart);
//        updates.put("timeEnd", timeEnd);
//        updates.put("maxAttendees", maxAttendees);
//        updates.put("description", description);
//        updates.put("posterPath", posterPath);
//        updates.put("maxWaitListEnabled", wantMaxWaitList);
//        updates.put("maxWaitList", maxWaitListInt);
//        updates.put("geoLocation", wantGeoLocation);
//        updates.put("randomSelect", wantRandomSelect);
//
//        eventRef.update(updates).addOnSuccessListener(aVoid ->
//                android.widget.Toast.makeText(getActivity(), "Event updated successfully!", android.widget.Toast.LENGTH_SHORT).show()
//        ).addOnFailureListener(e ->
//                android.widget.Toast.makeText(getActivity(), "Failed to update event.", android.widget.Toast.LENGTH_SHORT).show()
//        );
//    }
//
//    private void showGoBackDialog(View view) {
//        GoBackDialogFragment dialog = new GoBackDialogFragment();
//        dialog.setGoBackListener(() -> {
//            NavController nav = Navigation.findNavController(view);
//            nav.navigate(R.id.createEventstoMyEvents);
//        });
//        dialog.show(getParentFragmentManager(), "GoBackDialog");
//    }
}

