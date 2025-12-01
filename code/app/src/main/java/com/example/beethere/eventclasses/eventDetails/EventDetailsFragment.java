package com.example.beethere.eventclasses.eventDetails;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;

import com.example.beethere.device.DeviceIDViewModel;
import com.example.beethere.eventclasses.UserListManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class EventDetailsFragment extends Fragment {

    private Event event;
    private User user;
    private Boolean userCreated;
    private DeviceIDViewModel deviceID;
    private EventDataViewModel eventData;
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://beethere-images");

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate view
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        // checking device id status and defining user
        deviceID = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class); // get device id

        // get event and its data
        eventData = new ViewModelProvider(requireActivity()).get(EventDataViewModel.class);
        event = eventData.getEvent();

        userCreated = Boolean.FALSE;
        user = new User();
        checkUserDB();

        // SET LISTENERS

        // Go back to prev fragment
        Button prevButton = view.findViewById(R.id.event_detail_back_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        Button qrButton = view.findViewById(R.id.button_QR);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeFragment qrFragment = QRCodeFragment.newInstance(event.getEventID(), Boolean.TRUE);

                if (getContext() instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) getContext();
                    qrFragment.show(activity.getSupportFragmentManager(), "qrCodeDialog");
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        UserListManager eventListManager = new UserListManager(event);

        // Event Image
        ImageView imagePoster = view.findViewById(R.id.event_image);
        if (event.getPosterPath() != null) {
            Glide.with(getContext())
                    .load(event.getPosterPath()) // This is the download URL
                    //.placeholder(R.drawable.placeholder) // optional
                    //.error(R.drawable.error) // optional
                    .into(imagePoster);
        }

        // Event Name display
        TextView name = view.findViewById(R.id.text_event_name);
        name.setText(event.getTitle());

        // Event organizer display
        TextView organizer = view.findViewById(R.id.text_host_name);
        organizer.setText(event.getOrganizer().getName());

        // Event Description Display
        TextView description = view.findViewById(R.id.text_description);
        description.setText(event.getDescription());

        Button deletePosterButton = view.findViewById(R.id.posterDeleteButton);
        deletePosterButton.setVisibility(View.GONE);
        if (user.getAdmin()) {
            deletePosterButton.setVisibility(View.VISIBLE);

            deletePosterButton.setOnClickListener(v -> {
                StorageReference PosterRef = storage.getReferenceFromUrl(event.getPosterPath());
                PosterRef.delete();
            });
        }

        // Event Geoloc Req Display
        TextView geoLocReq = view.findViewById(R.id.text_geoloc_req);
        if (event.getGeoloc()) {
            geoLocReq.setText(getContext().getString(R.string.yes));
        } else {
            geoLocReq.setText(getContext().getString(R.string.no));
        }

        // Event Date display
        TextView date = view.findViewById(R.id.text_event_date);
        date.setText(String
                .format(
                        getContext().getString(R.string.event_date),
                        event.getEventDateStart(),
                        event.getEventDateEnd())
        );

        // Event Time display
        TextView time = view.findViewById(R.id.text_event_time);
        time.setText(String
                .format(
                        getContext().getString(R.string.event_time),
                        event.getEventTimeStart(),
                        event.getEventTimeEnd())
        );

        // Max Registered display
        TextView maxEnroll = view.findViewById(R.id.text_max_enroll);
        maxEnroll.setText(event.getEntrantMax().toString());

        // Number of people in waitlist
        TextView waitlist = view.findViewById(R.id.text_waitlist);
        waitlist.setText(eventListManager.waitlistSize().toString());



        // bottom display choices
        LocalDate currentDate = LocalDate.now();
        if(user == null){ // no profile connected to deviceID
            if (currentDate.isAfter(convertDate(event.getRegEnd(), dateFormatter))){
                // waitlist period ended display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));
            } else if (currentDate.isBefore(convertDate(event.getRegStart(), dateFormatter))){
                // waitlist period has not started display
                displayWaitlistStatus("Waitlist opens " + event.getRegStart());
            } else if (eventListManager.waitlistFull()) {
                // waitlist full display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_full));
            }  else {
                // waitlist button display that prompts create profile dialog
                displayWaitlistButton();
            }
        } else {
            // profile is connected to deviceID
            if(eventListManager.inRegistered(user)) {
                // user enrolled
                displayWaitlistStatus("Enrolled");
            } else if (eventListManager.isDeclined(user)) {
                // user declined, display waitlist ended
                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));
            } else if (eventListManager.inInvite(user)) {
                // user invited, accept or decline invite button

                InviteButtons button = new InviteButtons();

                button.setEvent(event);
                button.setUser(user);

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.button_layout, button).commit();

            } else if (currentDate.isBefore(convertDate(event.getRegStart(), dateFormatter))){
                // waitlist period has not started display
                displayWaitlistStatus("Waitlist opens " + event.getRegStart());
            } else if (currentDate.isAfter(convertDate(event.getRegEnd(), dateFormatter))){
                // waitlist period ended display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));
            } else if (eventListManager.waitlistFull()) {
                // waitlist full display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_full));
            } else {
                // join waitlist buttons added
                displayWaitlistButton();
            }
        }

        super.onViewCreated(view, savedInstanceState);
    }

    public void displayWaitlistButton(){
        WaitlistButtons button = new WaitlistButtons();

        button.setUser(user);
        button.setUserCreated(userCreated);
        button.setEvent(event);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.button_layout, button).commit();
    }

    public void displayWaitlistStatus(String status){
        StatusFragment statusFragment = new StatusFragment();

        statusFragment.setStatusText(status);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.button_layout, statusFragment).commit();
    }

    public LocalDate convertDate(String stringDate, DateTimeFormatter dateFormatter) {
        return LocalDate.parse(stringDate, dateFormatter);
    }

    public void checkUserDB(){

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID.getDeviceID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    // User does not exists related to deviceID
                    if (snapshot.exists()){
                        userCreated = Boolean.TRUE;
                        user = snapshot.toObject(User.class);
                    } else { // User does exist related to deviceID
                        //user = null;
                        userCreated = Boolean.FALSE;
                    }
                })
                .addOnFailureListener(fail ->
                        userCreated = Boolean.FALSE
                );
    }

}
