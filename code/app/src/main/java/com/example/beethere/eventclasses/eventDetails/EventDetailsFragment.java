package com.example.beethere.eventclasses.eventDetails;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.beethere.R;
import com.example.beethere.User;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;

import com.example.beethere.device.DeviceIDViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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



        // intialize value for if the user is created
        // set boolean to true or false?
        AtomicReference<Boolean> userCreated = new AtomicReference<>(Boolean.FALSE);
        // intialize user object
        // figure out alternative to setting a new user
        User user = new User("some name", "some email");

        // go through database and check device ID
        FirebaseFirestore.getInstance().collection("users").document(deviceID.getDeviceID())
                .get()
                .addOnSuccessListener((DocumentSnapshot snapshot) -> {
                    if (snapshot.exists()) {
                        //profle exists, so userCreated is true
                        // and user is intialized to profile of DeviceID
                        userCreated.set(Boolean.TRUE);
                        // TODO

                    } else {
                        // no profile for deviceID
                        // so userCreated is false
                        userCreated.set(Boolean.FALSE);
                    }
                })
                .addOnFailureListener(fail ->
                                userCreated.set(Boolean.FALSE)
                );


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

        ImageButton qrButton = view.findViewById(R.id.button_QR);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display image dialog fragment? for qr code
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Event Image
        ImageView imageView = view.findViewById(R.id.event_image);
        Bitmap bitmap = BitmapFactory.decodeFile(event.getPosterPath());
        imageView.setImageBitmap(bitmap);

        // Event Name display
        TextView name = view.findViewById(R.id.text_event_name);
        name.setText(event.getTitle());

        // Event organizer display
        TextView organizer = view.findViewById(R.id.text_host_name);
        organizer.setText(event.getOrganizer().getName());

        // Event Description Display
        TextView description = view.findViewById(R.id.text_description);
        description.setText(event.getDescription());

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
                        event.getEventTimeStart().toString(),
                        event.getEventTimeEnd().toString())
        );

        // Max Registered display
        TextView maxEnroll = view.findViewById(R.id.text_max_enroll);
        //maxEnroll.setText(event.getEntrantList().getMaxRegistered().toString());

        // Number of people in waitlist
        TextView waitlist = view.findViewById(R.id.text_waitlist);
        //waitlist.setText(event.getEntrantList().waitlistSize().toString());



        // bottom display choices
        LocalDate currentDate = LocalDate.now();
        if(!userCreated){ // no profile connected to deviceID
            if (currentDate.isAfter(convertDate(event.getRegEnd(), dateFormatter))){
                // waitlist period ended display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));

            } else if (Boolean.TRUE/*event.getEntrantList().waitlistFull()*/) {
                // waitlist full display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_full));

            }
            else {
                // waitlist button display that prompts create profile dialog
                displayWaitlistButton(user, userCreated);

            }
        } else {
            // profile is connected to deviceID
            if(/*event.getEntrantList().inRegistered(user)*/Boolean.TRUE) {
                // user enrolled
                displayWaitlistStatus("Enrolled");

            } else if (/*event.getEntrantList().isDeclined(user)*/Boolean.TRUE) {
                // user declined, display waitlist ended
                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));

            } else if (/*event.getEntrantList().inInvite(user)*/Boolean.TRUE) {
                // user invited, accept or decline invite button

                InviteButtons button = new InviteButtons();

                button.setEvent(event);
                button.setUser(user);

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.button_layout, button).commit();


            } else if (currentDate.isAfter(convertDate(event.getRegEnd(), dateFormatter))){
                // waitlist period ended display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));

            } else if (/*event.getEntrantList().waitlistFull()*/Boolean.TRUE) {
                // waitlist full display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_full));

            } else {
                // join waitlist buttons added
                displayWaitlistButton(user, userCreated);
            }
        }

        super.onViewCreated(view, savedInstanceState);
    }

    public void displayWaitlistButton(User user, Boolean created){
        WaitlistButtons button = new WaitlistButtons();

        button.setUser(user);
        button.setUserCreated(created);
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

}
