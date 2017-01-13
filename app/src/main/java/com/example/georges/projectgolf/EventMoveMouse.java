package com.example.georges.projectgolf;

import android.util.Log;

import java.util.Date;

/**
 * Created by Georges on 12/01/2017.
 */

public class EventMoveMouse {
    float mouseX,mouseY;
    long eventId;
    String eventDate;



    public EventMoveMouse(int mouseX, int mouseY, long eventId, String eventDate) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.eventId = eventId;
        this.eventDate = eventDate;
    }

    public EventMoveMouse() {

    }


    public void setMouseX(float mouseX) {
        this.mouseX = mouseX;
    }

    public void setMouseY(float mouseY) {
        this.mouseY = mouseY;
    }

    public void setEventlong(long eventDate) {
        this.eventId = eventDate;
    }

    public float getMouseX() {
        return mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    public long getEventLong() {
        return eventId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

}
