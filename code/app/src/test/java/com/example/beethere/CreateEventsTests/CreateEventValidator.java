package com.example.beethere.CreateEventsTests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CreateEventValidator {

    private static final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("hh:mm a");

    public static String validateInputs(
            String title, String regStart, String regEnd,
            String eventStart, String eventEnd,
            String timeStart, String timeEnd,
            String maxAttend, String description,
            String maxWaitList, boolean wantMaxWaitList,
            boolean hasImage
    ) {

        if (title.isEmpty() || regStart.isEmpty() || regEnd.isEmpty()
                || eventStart.isEmpty() || eventEnd.isEmpty()
                || timeStart.isEmpty() || timeEnd.isEmpty()
                || maxAttend.isEmpty() || description.isEmpty()
                || (wantMaxWaitList && maxWaitList.isEmpty())
                || !hasImage) {

            return "Please fill all required fields.";
        }

        if (description.length() > 500) {
            return "Description cannot be longer than 500 characters.";
        }

        if (title.length() > 24) {
            return "Title cannot be longer than 24 characters.";
        }

        try {
            LocalDate rs = LocalDate.parse(regStart, dateFormatter);
            LocalDate re = LocalDate.parse(regEnd, dateFormatter);
            LocalDate es = LocalDate.parse(eventStart, dateFormatter);
            LocalDate ee = LocalDate.parse(eventEnd, dateFormatter);

            LocalTime ts = LocalTime.parse(timeStart, timeFormatter);
            LocalTime te = LocalTime.parse(timeEnd, timeFormatter);

            int maxA = Integer.parseInt(maxAttend);
            int maxW = wantMaxWaitList && !maxWaitList.isEmpty()
                    ? Integer.parseInt(maxWaitList) : 0;

            if (wantMaxWaitList && maxW < maxA) {
                return "Number of wait-list entrants must exceed max attendees.";
            }

            if (rs.isAfter(re) || es.isAfter(ee)) {
                return "Ensure start date is before end date.";
            }

            if (ts.isAfter(te)) {
                return "Ensure start time is before end time.";
            }

            if (re.isEqual(es) || re.isAfter(es)) {
                return "Registration and event dates cannot overlap.";
            }

        } catch (Exception e) {
            return "Ensure input formats are correct.";
        }

        return null; // no errors
    }
}

