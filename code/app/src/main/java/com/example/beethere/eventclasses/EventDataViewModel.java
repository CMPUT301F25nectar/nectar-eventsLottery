package com.example.beethere.eventclasses;

import androidx.lifecycle.ViewModel;

public class EventDataViewModel extends ViewModel {
    private Event event;

    public EventDataViewModel(){
        this.event = null;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
