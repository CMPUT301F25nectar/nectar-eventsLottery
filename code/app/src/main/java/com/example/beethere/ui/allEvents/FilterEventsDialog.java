package com.example.beethere.ui.allEvents;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.beethere.R;
import com.example.beethere.ui.myEvents.TimePickerFragment;

import java.util.Calendar;

public class FilterEventsDialog extends DialogFragment {

    public interface FilterDialogListener {
        void onFilterApplied(String regStart, String regEnd, String eventStart,
                             String eventEnd, String timeStart, String timeEnd);
    }

    private FilterDialogListener filterListener;

    public FilterEventsDialog(FilterDialogListener listener) {
        this.filterListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_filter_events, null);
        builder.setView(view);

        EditText regStart = view.findViewById(R.id.filter_registration_start_date);
        EditText regEnd = view.findViewById(R.id.filter_registration_end_date);
        EditText eventStart = view.findViewById(R.id.filter_event_start_date);
        EditText eventEnd = view.findViewById(R.id.filter_event_end_date);
        EditText timeStart = view.findViewById(R.id.filter_start_time);
        EditText timeEnd = view.findViewById(R.id.filter_end_time);

        View.OnClickListener dateClickListener = v -> showDatePicker((EditText) v);
        regStart.setOnClickListener(dateClickListener);
        regEnd.setOnClickListener(dateClickListener);
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

        Button cancel = view.findViewById(R.id.filter_cancel_button);
        cancel.setOnClickListener(v -> dismiss());

        Button apply = view.findViewById(R.id.filter_apply_button);
        apply.setOnClickListener(v -> {
            String regStartInput = regStart.getText().toString().trim();
            String regEndInput = regEnd.getText().toString().trim();
            String eventStartInput = eventStart.getText().toString().trim();
            String eventEndInput = eventEnd.getText().toString().trim();
            String timeStartInput = timeStart.getText().toString().trim();
            String timeEndInput = timeEnd.getText().toString().trim();

            if (filterListener != null) {
                filterListener.onFilterApplied(regStartInput, regEndInput,
                        eventStartInput, eventEndInput,
                        timeStartInput, timeEndInput);
            }
            dismiss();
        });

        return builder.create();
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
}