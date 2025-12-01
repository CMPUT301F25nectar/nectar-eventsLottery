package com.example.beethere.ui.myEvents;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.beethere.DatabaseFunctions;
import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class EditingEventFragment extends Fragment {

    private ImageView eventPoster;
    public Uri imageURL;

    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://beethere-images");
    private StorageReference storageReference;

    private DatabaseFunctions dbFunctions = new DatabaseFunctions();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText eventTitle, regStart, regEnd, eventStart,
            eventEnd, timeStart, timeEnd, eventDesc;
    private AppCompatButton deleteEventButton;
    private TextView errorMessage;
    private boolean isSubmitting = false;

    private ArrayList<Event> events;
    private static final String ARG_EVENT_ID = "eventID";

    private Event event;
    private String eventID;

    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

    private ActivityResultLauncher<String> pickImageLauncher;

    /**
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = getArguments().getString(ARG_EVENT_ID);
        }
    }

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        loadEventData();

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

        eventPoster.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

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

            if (currentDestination == R.id.navigation_edit_event) {
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


    private void loadEventData() {
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
                            Glide.with(requireContext())
                                    .load(event.getPosterPath())
                                    .placeholder(R.drawable.placeholder_event_poster)
                                    .error(R.drawable.placeholder_event_poster)
                                    .into(eventPoster);
                    }
                    setupDatePickers();
                });
            }


    private void setupDatePickers() {
        LocalDate regStartDate = LocalDate.parse(event.getRegStart(), dateFormatter);
        LocalDate regEndDate = LocalDate.parse(event.getRegEnd(), dateFormatter);
        LocalDate eventStartDate = LocalDate.parse(event.getEventDateStart(), dateFormatter);

        if (!currentDate.isBefore(regStartDate)) {
            regStart.setEnabled(false);
            regStart.setBackgroundResource(R.drawable.test_input_gray_background);
        } else {
            regStart.setOnClickListener(v -> showDatePicker(regStart));
        }

        if (!currentDate.isBefore(regEndDate)) {
            regEnd.setEnabled(false);
            regEnd.setBackgroundResource(R.drawable.test_input_gray_background);
        } else {
            regEnd.setOnClickListener(v -> showDatePicker(regEnd));
        }

        if (currentDate.isAfter(eventStartDate)) {
            regStart.setEnabled(false);
            regEnd.setEnabled(false);
            eventStart.setEnabled(false);
            eventEnd.setEnabled(false); // unsure of this, i could just mkae it so that if the current date is after then event end then tahts a seperate condition

            regStart.setBackgroundResource(R.drawable.test_input_gray_background);
            regEnd.setBackgroundResource(R.drawable.test_input_gray_background);
            eventStart.setBackgroundResource(R.drawable.test_input_gray_background);
            eventEnd.setBackgroundResource(R.drawable.test_input_gray_background);
        } else {
            regEnd.setOnClickListener(v -> showDatePicker(regEnd));
            regStart.setOnClickListener(v -> showDatePicker(regStart));
            eventStart.setOnClickListener(v -> showDatePicker(eventStart));
            eventEnd.setOnClickListener(v -> showDatePicker(eventEnd));
        }

    }

    /**
     *
     * @param itemId takes in itemID if user switches through bottom navigation
     */
    private void navigateTo(final int itemId) {
        NavController nav = NavHostFragment.findNavController(this); // safer
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
        nav.navigate(R.id.EditEventsToMyEvents);
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

    @SuppressLint("SetTextI18n")
    public void complete(View view) {

        AppCompatButton completeButton = getView().findViewById(R.id.completeButton);

        if (isSubmitting) return;
        isSubmitting = true;

        completeButton.setEnabled(false);
        completeButton.setText("Updating…");

        String newTitle = eventTitle.getText().toString().trim();
        String newRegStart = regStart.getText().toString().trim();
        String newRegEnd = regEnd.getText().toString().trim();
        String newStartDate = eventStart.getText().toString().trim();
        String newEndDate = eventEnd.getText().toString().trim();
        String newTimeStart = timeStart.getText().toString().trim();
        String newTimeEnd = timeEnd.getText().toString().trim();
        String newDesc = eventDesc.getText().toString().trim();

        Runnable resetButton = () -> {
            isSubmitting = false;
            completeButton.setEnabled(true);
            completeButton.setText("Update");
        };


        if (newTitle.isEmpty() || newRegStart.isEmpty() || newRegEnd.isEmpty() ||
                newStartDate.isEmpty() || newEndDate.isEmpty() ||
                newTimeStart.isEmpty() || newTimeEnd.isEmpty() || newDesc.isEmpty()) {
            showErrorMessage("Please fill all required fields.");
            resetButton.run();
            return;
        }

        if (newDesc.length() > 500) {
            showErrorMessage("Description cannot be longer than 500 characters.");
            resetButton.run();
            return;
        }

        if (newTitle.length() > 24) {
            showErrorMessage("Title cannot be longer than 24 characters.");
            resetButton.run();
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
                showErrorMessage("Ensure start date is before end date.");
                resetButton.run();
                return;
            }

            if (startTime.isAfter(endTime)) {
                showErrorMessage("Ensure start time is before end time.");
                resetButton.run();
                return;
            }

            if (!regEndDate.isBefore(eventStartDate)) {
                showErrorMessage("Registration must close before event starts.");
                resetButton.run();
                return;
            }

            if (imageURL != null) {

                StorageReference newPosterRef =
                        storageReference.child("images/" + UUID.randomUUID() + ".jpg");

                newPosterRef.putFile(imageURL)
                        .addOnSuccessListener(taskSnapshot ->
                                newPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                    String newDownloadUrl = uri.toString();
                                    String oldPosterUrl = event.getPosterPath();

                                    updateEvent(view, newTitle, newRegStart, newRegEnd,
                                            newStartDate, newEndDate,
                                            newTimeStart, newTimeEnd, newDesc,
                                            newDownloadUrl);

                                    StorageReference oldPosterRef =
                                            storage.getReferenceFromUrl(oldPosterUrl);

                                    oldPosterRef.delete()
                                            .addOnSuccessListener(v -> {
                                                Log.d("FirebaseStorage", "Old image deleted.");

                                                backMain(view);
                                                showSnackbar("Event updated successfully!");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("FirebaseStorage",
                                                        "Failed to delete old image: " + e.getMessage());
                                                backMain(view);
                                                showSnackbar("Event updated successfully!");
                                            });

                                }))
                        .addOnFailureListener(e -> {
                            showErrorMessage("Upload Error: " + e.getMessage());
                            resetButton.run();
                        });

            } else {
                updateEvent(view, newTitle, newRegStart, newRegEnd,
                        newStartDate, newEndDate, newTimeStart, newTimeEnd,
                        newDesc, event.getPosterPath());

                backMain(view);
                showSnackbar("Event updated successfully!");
            }

        } catch (Exception e) {
            showErrorMessage("Ensure input formats are correct.");
        }
    }

    /**
     *
     * @param titleInput entered title
     * @param regStartInput entered registration begin date
     * @param regEndInput entered registration end date
     * @param eventStartInput entered event start date
     * @param eventEndInput entered event end date
     * @param timeStartInput entered event time start date
     * @param timeEndInput entered event time end date
     * @param descInput entered description
     * @param imageInput entered image
     */
    private void updateEvent(View view, String titleInput, String regStartInput, String regEndInput,
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

        if (!Objects.equals(imageInput, event.getPosterPath())) {
            event.setPosterPath(imageInput);
        }
        updateDB();
    }


    private void updateDB() {
        db.collection("events")
                .document(eventID)
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EditingEvent", "Event updated successfully.");
                    showSnackbar("Event updated successfully.");

                        })
                .addOnFailureListener(e -> {
                    Log.e("EditingEvent", "Failed to update: " + e.getMessage());
                    showSnackbar("Failed to update event.");
                });
    }

    private void showDeleteDialog() {
        ConfirmDeleteFragment dialog = new ConfirmDeleteFragment(eventID);
        dialog.setOnEventDeletedListener(id -> {
            View fragmentView = getView();
            if (fragmentView != null) {
                NavController navController = Navigation.findNavController(fragmentView);
                navController.navigate(R.id.EditEventsToMyEvents);
            }
        });
        dialog.show(getParentFragmentManager(), "ConfirmDelete");
    }
    /**
     *
     * @param message message to display at the bottom of view
     */
    private void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    public void showSnackbar(String text){
        View rootView = getView();
        if (rootView == null) return;

        Snackbar snackbar = Snackbar.make(rootView, text, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getContext().getColor(R.color.dark_brown))
                .setTextColor(getContext().getColor(R.color.yellow));

        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        snackbarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbarText.setTextSize(20);
        snackbarText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.work_sans_semibold));

        snackbar.show();
    }
}
