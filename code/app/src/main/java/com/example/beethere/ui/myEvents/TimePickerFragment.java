package com.example.beethere.ui.myEvents;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private EditText targetEditText;

    public TimePickerFragment(EditText targetEditText) {
        this.targetEditText = targetEditText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(
                getActivity(),
                this,
                hour,
                minute,
                false   // force 12-hour mode for AM/PM formatting
        );
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        String formatted = sdf.format(calendar.getTime());

        targetEditText.setText(formatted);
    }
}
