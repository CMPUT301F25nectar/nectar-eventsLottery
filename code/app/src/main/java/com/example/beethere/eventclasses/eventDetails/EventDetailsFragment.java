package com.example.beethere.eventclasses.eventDetails;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class EventDetailsFragment extends Fragment {
//
//    private Event event;
//    private DeviceIDViewModel deviceID;
//    private EventDataViewModel eventData;
//
//
//    public Event getEvent() {
//        return event;
//    }
//    public void setEvent(Event event) {
//        this.event = event;
//    }
//
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
//        eventData = new ViewModelProvider(requireActivity()).get(EventDataViewModel.class);
//        event = eventData.getEvent();
//
//        // checking device id status and defining user
//        // get device id
//        deviceID = new ViewModelProvider(requireActivity()).get(DeviceIDViewModel.class);
//
//        // intialize value for if the user is created
//        AtomicReference<Boolean> userCreated = new AtomicReference<>(Boolean.FALSE);
//        // intialize user object
//        User user = new User("some name", "some email");
//        // go through database and check device ID
//        FirebaseFirestore.getInstance().collection("users").document(deviceID.getDeviceID())
//                .get()
//                .addOnSuccessListener((DocumentSnapshot snapshot) -> {
//                    if (snapshot.exists()) {
//                        //profle exists, so userCreated is true
//                        // and user is intialized to profile of DeviceID
//                        userCreated.set(Boolean.TRUE);
//                        // TODO
//
//                    } else {
//                        // no profile for deviceID
//                        // so userCreated is false
//                        userCreated.set(Boolean.FALSE);
//                    }
//                })
//                .addOnFailureListener(fail ->
//                                userCreated.set(Boolean.FALSE)
//                );
//
//        // Event Name display
//        TextView name = view.findViewById(R.id.text_event_name);
//        name.setText(event.getTitle());
//
//        // Event organizer display
//        TextView organizer = view.findViewById(R.id.text_host_name);
//        organizer.setText(String
//                .format(
//                        getContext().getString(R.string.event_organizer),
//                        event.getOrganizer().getName())
//        );
//
//        // Event Description Display
//        TextView description = view.findViewById(R.id.text_description);
//        description.setText(event.getDescription());
//
//        // Event Geoloc Req Display
//        TextView geoLocReq = view.findViewById(R.id.text_geoloc_req);
//        if (event.getGeoloc()) {
//            geoLocReq.setText(getContext().getString(R.string.yes));
//        } else {
//            geoLocReq.setText(getContext().getString(R.string.no));
//        }
//
//        // Event Date display
//        TextView date = view.findViewById(R.id.text_event_date);
//        date.setText(String
//                .format(
//                        getContext().getString(R.string.event_date),
//                        event.getEventDateStart().toString(),
//                        event.getEventDateEnd().toString())
//        );
//
//        // Event Time display
//        TextView time = view.findViewById(R.id.text_event_time);
//        time.setText(String
//                .format(
//                        getContext().getString(R.string.event_time),
//                        event.getEventTimeStart().toString(),
//                        event.getEventTimeEnd().toString())
//        );
//
//        // Max Registered display
//        TextView maxEnroll = view.findViewById(R.id.text_max_enroll);
//        maxEnroll.setText(event.getEntrantList().getMaxRegistered().toString());
//
//        // Number of people in waitlist
//        TextView waitlist = view.findViewById(R.id.text_waitlist);
//        waitlist.setText(event.getEntrantList().waitlistSize().toString());
//
//        // TODO
//        // event image set up
//        ImageView imageView = view.findViewById(R.id.event_image);
//        Bitmap bitmap = BitmapFactory.decodeFile(event.getPosterPath());
//        imageView.setImageBitmap(bitmap);
//
//
//        // SET LISTENERS
//
//        // Go back to event list
//        FloatingActionButton fab = view.findViewById(R.id.button_back);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getParentFragmentManager();
//                fragmentManager.popBackStack();
//            }
//        });
//
//        ImageButton qrButton = view.findViewById(R.id.button_QR);
//        qrButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //display image dialog fragment? for qr code
//            }
//        });
//
//
//
//        LocalDate currentDate = LocalDate.now();
//        if(!userCreated.get()){ // no profile connected to deviceID
//            if (currentDate.isAfter(event.getRegEnd())){ // waitlist period ended display
//                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));
//            } else if (event.getEntrantList().waitlistFull()) { // waitlist full display
//                displayWaitlistStatus(getContext().getString(R.string.waitlist_full));
//            }
//            else { // waitlist button display that prompts create profile dialog
//                displayWaitlistButton(user, userCreated.get());
//            }
//        } else {
//            if(event.getEntrantList().inRegistered(user)) { // user enrolled
//                displayWaitlistStatus("Enrolled");
//            } else if (event.getEntrantList().isDeclined(user)) { // user declined, display waitlist ended
//                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));
//            } else if (event.getEntrantList().inInvite(user)) { // user invited, accept or decline invite button
//                InviteButtons button = new InviteButtons();
//                button.setEvent(event);
//                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                transaction.add(R.id.button_layout, button).commit();
//            } else if (currentDate.isAfter(event.getRegEnd())){ // waitlist period ended display
//                displayWaitlistStatus(getContext().getString(R.string.waitlist_ended));
//            } else if (event.getEntrantList().waitlistFull()) { // waitlist full display
//                displayWaitlistStatus(getContext().getString(R.string.waitlist_full));
//            } else { // join waitlist buttons added
//                displayWaitlistButton(user, userCreated.get());
//            }
//        }
//
//        return view;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        getParentFragmentManager().beginTransaction().remove(EventDetailsFragment.this).commitAllowingStateLoss();
//    }
//
//    public void displayWaitlistButton(User user, Boolean created){
//        WaitlistButtons button = new WaitlistButtons();
//        button.setUser(user);
//        button.setUserCreated(created);
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.add(R.id.button_layout, button).commit();
//    }
//
//    public void displayWaitlistStatus(String status){
//        StatusFragment statusFragment = new StatusFragment();
//        statusFragment.setStatusText(status);
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.add(R.id.button_layout, statusFragment).commit();
//    }
}
