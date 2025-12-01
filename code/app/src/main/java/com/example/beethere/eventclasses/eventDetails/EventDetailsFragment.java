package com.example.beethere.eventclasses.eventDetails;

import android.os.Bundle;
import android.util.Log;
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
import com.example.beethere.DatabaseCallback;
import com.example.beethere.DatabaseFunctions;
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

/**
 *
 */
public class EventDetailsFragment extends Fragment {

    private Event event;
    private User user;
    private DeviceIDViewModel deviceID;
    private EventDataViewModel eventData;
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://beethere-images");

    private DateTimeFormatter dateFormatter;
    private UserListManager eventManager;
    private ImageButton deletePosterButton;

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
        eventManager = new UserListManager(event);

        // establish formatter
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        user = new User();
        checkUserDB();

        // SET LISTENERS

        // Go back to prev fragment
        Button prevButton = view.findViewById(R.id.event_detail_back_button);
        prevButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        Button qrButton = view.findViewById(R.id.button_QR);
        qrButton.setOnClickListener(v -> {
            QRCodeFragment qrFragment = QRCodeFragment.newInstance(event.getEventID(), Boolean.TRUE);

            if (getContext() instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) getContext();
                qrFragment.show(activity.getSupportFragmentManager(), "qrCodeDialog");
            }
        });

        // Event Image
        ImageView imagePoster = view.findViewById(R.id.event_image);
        Glide.with(requireContext()).clear(imagePoster);
        Glide.with(requireContext())
                .load(event.getPosterPath())
                .placeholder(R.drawable.placeholder_event_poster)
                .error(R.drawable.placeholder_event_poster)
                .into(imagePoster);

        // Event Name display
        TextView name = view.findViewById(R.id.text_event_name);
        name.setText(event.getTitle());

        // Event organizer display
        TextView organizer = view.findViewById(R.id.text_host_name);
        organizer.setText(event.getOrganizer().getName());

        // Event Description Display
        TextView description = view.findViewById(R.id.text_description);
        description.setText(event.getDescription());

        deletePosterButton = view.findViewById(R.id.posterDeleteButton);
        deletePosterButton.setVisibility(View.GONE);


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
        waitlist.setText(eventManager.waitlistSize().toString());

        // setBottomDisplay called after checking user callback

        super.onViewCreated(view, savedInstanceState);
    }

    public void setBottomDisplay(){
        LocalDate currentDate = LocalDate.now();
        LocalDate regStart = convertDate(event.getRegStart(), dateFormatter);
        LocalDate regEnd = convertDate(event.getRegEnd(), dateFormatter);
        String waitlistEnded = getContext().getString(R.string.waitlist_ended);

        if (user == null || user.getDeviceid() == null){
            if (currentDate.isAfter(regEnd)){
                // waitlist period ended display
                displayWaitlistStatus(waitlistEnded);
            } else if (currentDate.isBefore(regStart)){
                // waitlist period has not started display
                displayWaitlistStatus("Waitlist opens " + event.getRegStart());
            } else if (eventManager.waitlistFull()) {
                // waitlist full display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_full));
            }  else {
                // waitlist button display that prompts create profile dialog
                displayWaitlistButton();
            }
        } else { // user exists
            // user enrolled
            if (eventManager.inRegistered(user)) { // user enrolled
                displayWaitlistStatus("Enrolled");
            } else if (eventManager.isDeclined(user)) { // user was invited and declined
                // user declined, display waitlist ended
                displayWaitlistStatus(waitlistEnded);
            } else if (eventManager.inInvite(user)) { // user invited, hasn't interacted with invite
                displayInviteButtons();
            } else if (currentDate.isBefore(regStart)){ // waitlist has not started yet
                // waitlist period has not started display
                displayWaitlistStatus("Waitlist opens " + event.getRegStart());
            } else if (currentDate.isAfter(regEnd)){
                // waitlist period ended display
                displayWaitlistStatus(waitlistEnded);
            } else if (eventManager.waitlistFull()) {
                // waitlist full display
                displayWaitlistStatus(getContext().getString(R.string.waitlist_full));
            } else {
                // join waitlist buttons added
                displayWaitlistButton();
            }
        }
    }

    public void displayInviteButtons(){
        InviteButtons button = new InviteButtons();

        button.setEvent(event);
        button.setUser(user);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.button_layout, button).commit();
    }

    public void displayWaitlistButton(){
        WaitlistButtons button = new WaitlistButtons();

        button.setUser(user);
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

        DatabaseCallback<User> userCallback = new DatabaseCallback<User>() {
            @Override
            public void onCallback(User result) {
                user = result;
                if (Boolean.TRUE.equals(user.getAdmin())) {
                    deletePosterButton.setVisibility(View.VISIBLE);

                    if(event.getPosterPath() != null && !event.getPosterPath().isEmpty()){
                        deletePosterButton.setOnClickListener(v -> {
                            StorageReference PosterRef = storage.getReferenceFromUrl(event.getPosterPath());
                            PosterRef.delete(); //TODO handel confirm delete and some on sucofail
                        });
                    }
                }
                setBottomDisplay();
            }
            @Override
            public void onError(Exception e) {
                user = null;
                setBottomDisplay();
                Log.d("EventDetails", "Error getting user in eventDetails");
            }
        };

        DatabaseFunctions db = new DatabaseFunctions();
        db.getUserDB(deviceID.getDeviceID(), userCallback);
    }

}
