package com.example.beethere.eventclasses.eventDetails;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.beethere.R;
import com.example.beethere.eventclasses.Event;
import com.example.beethere.eventclasses.EventDataViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeFragment extends DialogFragment {

    private String eventID;
    private ImageView qrCode;
    private AppCompatButton exit,  scan;
    private Event event;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Boolean eventDetails;


    public static QRCodeFragment newInstance(String eventID, Boolean eventDetails) {
        QRCodeFragment fragment = new QRCodeFragment();
        Bundle args = new Bundle();
        args.putString("eventID", eventID);
        args.putBoolean("eventDetails", eventDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (300 * getResources().getDisplayMetrics().density);
            int height = (int) (400 * getResources().getDisplayMetrics().density);

            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = getArguments().getString("eventID");
            eventDetails = getArguments().getBoolean("eventDetails");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_qrcode, container, false);

        qrCode = view.findViewById(R.id.qrCode);
        exit = view.findViewById(R.id.qrExit);
        scan = view.findViewById(R.id.scanButton);
        if(eventDetails){
            scan.setVisibility(View.INVISIBLE);
        }

        EventDataViewModel eventDataViewModel =
                new ViewModelProvider(requireActivity()).get(EventDataViewModel.class);


        generateQRCode(eventID);

        exit.setOnClickListener(v -> dismiss());

        scan.setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) qrCode.getDrawable()).getBitmap();
            String scannedData = decodeQRCode(bitmap);

            if (scannedData != null) {
                showSnackbar(view, "Scanned: " + scannedData);

                db.collection("events")
                        .document(scannedData)
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            event = snapshot.toObject(Event.class);

                            if (event == null) {
                                showSnackbar(view, "Error: Event Not Found");
                                return;
                            }

                            eventDataViewModel.setEvent(event);

                            NavController nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                            nav.navigate(R.id.MyEventsToEventDetails);
                            dismiss(); // close the dialog after navigation

                        })
                        .addOnFailureListener(e ->
                                showSnackbar(view, "Error Loading Event")
                        );
            } else {
                showSnackbar(view, "Error: Event Details Not Found");
            }
        });

        return view;
    }

    private void generateQRCode(String text) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 270, 270);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private String decodeQRCode(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result result = new MultiFormatReader().decode(binaryBitmap);
            return result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void showSnackbar(View view, String text){
        Snackbar snackbar = Snackbar.make(view,text, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getContext().getColor(R.color.dark_brown))
                .setTextColor(getContext().getColor(R.color.yellow));
        View snackbarView = snackbar.getView();
        TextView snackbarText = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        snackbarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbarText.setTextSize(20);
        snackbarText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.work_sans_semibold));
        snackbar.show();
    }

}
