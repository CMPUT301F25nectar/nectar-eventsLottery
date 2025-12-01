package com.example.beethere.CreateEventsTests;

import org.junit.Test;
import static org.junit.Assert.*;

public class CreateEventFragment1Test {

    private final String validDate = "01/01/2025";
    private final String laterDate = "05/01/2025";
    private final String validTime = "10:00 AM";
    private final String laterTime = "12:00 PM";

    //when all fields are empty
    @Test
    public void testMissingRequiredFields() {
        String result = CreateEventValidator.validateInputs(
                "", "", "", "", "", "", "",
                "", "", "", false, false
        );
        assertEquals("Please fill all required fields.", result);
    }

    // when description is too long
    @Test
    public void testDescriptionTooLong() {
        String longDesc = "a".repeat(501);

        String result = CreateEventValidator.validateInputs(
                "Event", validDate, laterDate,
                validDate, laterDate,
                validTime, laterTime,
                "10", longDesc, "20",
                false, true
        );

        assertEquals("Description cannot be longer than 500 characters.", result);
    }

    // when title is too long
    @Test
    public void testTitleTooLong() {
        String longTitle = "a".repeat(25);

        String result = CreateEventValidator.validateInputs(
                longTitle, validDate, laterDate,
                validDate, laterDate,
                validTime, laterTime,
                "10", "desc", "20",
                false, true
        );

        assertEquals("Title cannot be longer than 24 characters.", result);
    }

    // registration date logic
    @Test
    public void testRegStartAfterRegEnd() {
        String result = CreateEventValidator.validateInputs(
                "Title", "10/01/2025", "05/01/2025",
                validDate, laterDate,
                validTime, laterTime,
                "10", "desc", "20",
                false, true
        );

        assertEquals("Ensure start date is before end date.", result);
    }

    // event date logic
    @Test
    public void testEventStartAfterEventEnd() {
        String result = CreateEventValidator.validateInputs(
                "Title", validDate, laterDate,
                "10/01/2025", "05/01/2025",
                validTime, laterTime,
                "10", "desc", "20",
                false, true
        );

        assertEquals("Ensure start date is before end date.", result);
    }

    // registration end cant overlap
    @Test
    public void testDateOverlap() {
        String result = CreateEventValidator.validateInputs(
                "Title", validDate, "10/01/2025",
                "10/01/2025", "11/01/2025",
                validTime, laterTime,
                "10", "desc", "20",
                false, true
        );

        assertEquals("Registration and event dates cannot overlap.", result);
    }

    // time logic
    @Test
    public void testTimeInvalid() {
        String result = CreateEventValidator.validateInputs(
                "Title", validDate, laterDate,
                validDate, laterDate,
                "05:00 PM", "04:00 PM",
                "10", "desc", "20",
                false, true
        );

        assertEquals("Ensure start time is before end time.", result);
    }

    // waitlist enabled but the field is empty
    @Test
    public void testWaitListEnabledButEmpty() {
        String result = CreateEventValidator.validateInputs(
                "Title", validDate, laterDate,
                validDate, laterDate,
                validTime, laterTime,
                "10", "desc", "",
                true, true
        );

        assertEquals("Please fill all required fields.", result);
    }

    // waitlist is > max attendees
    @Test
    public void testWaitListLessThanAttendees() {
        String result = CreateEventValidator.validateInputs(
                "Title", validDate, laterDate,
                validDate, laterDate,
                validTime, laterTime,
                "20", "desc", "10",
                true, true
        );

        assertEquals("Number of wait-list entrants must exceed max attendees.", result);
    }

    //  no image
    @Test
    public void testMissingImage() {
        String result = CreateEventValidator.validateInputs(
                "Title", validDate, laterDate,
                validDate, laterDate,
                validTime, laterTime,
                "10", "desc", "20",
                false, false
        );

        assertEquals("Please fill all required fields.", result);
    }

    // wrong number format
    @Test
    public void testInvalidNumberFormat() {
        String result = CreateEventValidator.validateInputs(
                "Title", validDate, laterDate,
                validDate, laterDate,
                validTime, laterTime,
                "abc", "desc", "20",
                false, true
        );

        assertEquals("Ensure input formats are correct.", result);
    }

    // valid input
    @Test
    public void testValidInput() {
        String result = CreateEventValidator.validateInputs(
                "Good Title", "01/01/2025", "02/01/2025",
                "05/01/2025", "06/01/2025",
                "10:00 AM", "12:00 PM",
                "10", "desc", "20",
                true, true
        );

        assertNull(result);
    }
}

