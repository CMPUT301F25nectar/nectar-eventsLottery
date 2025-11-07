package com.example.beethere.CreateEventsTests;

import org.junit.Test;
import static org.mockito.Mockito.*;

import com.example.beethere.ui.myEvents.CreateEventFragment1;

import static org.junit.Assert.*;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;

import org.junit.Test;

import java.lang.reflect.Field;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Test;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class CreateEventFragment1Test {

    @Test
    public void testComplete_withEmptyFields_showsErrorMessage() throws NoSuchFieldException, IllegalAccessException {
        // Create the fragment instance
        CreateEventFragment1 fragment = new CreateEventFragment1();

        // Use reflection to access private fields and set them
        setPrivateField(fragment, "eventTitle", "");
        setPrivateField(fragment, "regStart", "");
        setPrivateField(fragment, "regEnd", "");
        setPrivateField(fragment, "eventStart", "");
        setPrivateField(fragment, "eventEnd", "");
        setPrivateField(fragment, "timeStart", "");
        setPrivateField(fragment, "timeEnd", "");
        setPrivateField(fragment, "maxAttend", "");
        setPrivateField(fragment, "eventDesc", "");
        setPrivateField(fragment, "maxWaitList", "");

        // Simulate form completion (button click)
        fragment.complete();

        // Verify that the error message is shown
        assertTrue(fragment.errorMessage.getVisibility() == View.VISIBLE);
    }

    @Test
    public void testComplete_withValidInput_savesEventToDatabase() throws NoSuchFieldException, IllegalAccessException {
        CreateEventFragment1 fragment = new CreateEventFragment1();

        setPrivateField(fragment, "eventTitle", "Sample Event");
        setPrivateField(fragment, "regStart", "01/01/2025");
        setPrivateField(fragment, "regEnd", "10/01/2025");
        setPrivateField(fragment, "eventStart", "15/01/2025");
        setPrivateField(fragment, "eventEnd", "20/01/2025");
        setPrivateField(fragment, "timeStart", "10:00 AM");
        setPrivateField(fragment, "timeEnd", "04:00 PM");
        setPrivateField(fragment, "maxAttend", "100");
        setPrivateField(fragment, "eventDesc", "This is a test event.");
        setPrivateField(fragment, "maxWaitList", "50");

        fragment.imageURL = Uri.parse("file://someimage.jpg");

        fragment.complete();

        assertFalse(fragment.errorMessage.getVisibility() == View.VISIBLE);

        assertNotNull(fragment.events);
        assertEquals(1, fragment.events.size());
    }

    private void setPrivateField(CreateEventFragment1 fragment, String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field = CreateEventFragment1.class.getDeclaredField(fieldName);
        field.setAccessible(true);  // Make the field accessible
        if (field.getType().equals(String.class)) {
            field.set(fragment, value);
        } else if (field.getType().equals(EditText.class)) {
            EditText editText = (EditText) field.get(fragment);
            editText.setText(value);
        }
    }

}



