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
import android.widget.CompoundButton;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.beethere.DeviceId;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.example.beethere.eventclasses.UserListManager;
import com.example.beethere.ui.device.DeviceIDViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.example.beethere.DatabaseFunctions;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CreateEventFragment1 extends Fragment {

    private ImageView eventPoster;
    public Uri imageURL;

    private User organizer;
    private DeviceIDViewModel deviceIDProvider;


    private boolean wantMaxWaitList, wantRandomSelect, wantGeoLocation = false;

    private EditText eventTitle, regStart, regEnd, eventStart,
            eventEnd, timeStart, timeEnd, maxAttend, eventDesc, maxWaitList;
    private SwitchCompat maxWaitListSwitch, randomSelectSwitch, geoLocationSwitch;
    public TextView errorMessage;
    private MyEventsAdapter myEventsAdapter;
    public ArrayList<Event> events;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseFunctions dbFunctions = new DatabaseFunctions();


    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);  // Use Locale.US for AM/PM format


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

        String deviceID = DeviceId.get(requireContext());
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
                errorMessage.setText("Ensure start dates/times are before end dates/times.");
                errorMessage.setVisibility(View.VISIBLE);
            }

            addToDatabase(titleInput, regStartDate, regEndDate, eventStartDate, eventEndDate,
                    startTime, endTime, maxAttendeesInt, descInput, imageURL.toString(),
                    wantMaxWaitList, wantGeoLocation, wantRandomSelect, maxWaitListInt);

            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
            android.widget.Toast.makeText(getActivity(), "Event created successfully!", android.widget.Toast.LENGTH_SHORT).show();



        } catch (Exception e) {
            Log.e("CreateEventFragment", "Parsing error: " + e.getMessage());
            errorMessage.setText("Ensure input formats are correct.");
            errorMessage.setVisibility(View.VISIBLE);
        }

    }

    private void addToDatabase(String title, LocalDate regStart, LocalDate regEnd, LocalDate eventStart,
                               LocalDate eventEnd, LocalTime timeStart, LocalTime timeEnd, int maxAttendees,
                               String description, String posterPath, boolean wantMaxWaitList, boolean wantGeoLocation,
                               boolean wantRandomSelect, int maxWaitListInt) throws WriterException {

        DocumentReference newEventRef = db.collection("events").document(); // auto-generated ID
        String eventID = newEventRef.getId();

        boolean status = true;


        //TODO: add QR code to the collection of QR codes

        if (wantMaxWaitList) {
            Event event = new Event(organizer, eventID, title, description, posterPath,
                    status, regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
                    maxAttendees, wantGeoLocation, wantRandomSelect);
            dbFunctions.addEventDB(event);
            events.add(event);
            myEventsAdapter.notifyDataSetChanged();

        } else {
            Event event = new Event(organizer, eventID, title, description, posterPath,
                    status, regStart, regEnd, eventStart, eventEnd, timeStart, timeEnd,
                    maxAttendees, wantGeoLocation, wantRandomSelect, maxWaitListInt);

            dbFunctions.addEventDB(event);
            events.add(event);
            myEventsAdapter.notifyDataSetChanged();
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // You can use PNG or JPEG format
        return stream.toByteArray();
    }

    private Bitmap convertBitMatrixToBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }
}











