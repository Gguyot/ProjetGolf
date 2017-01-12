package com.example.georges.projectgolf;

import android.util.Log;

import java.util.Date;

/**
 * Created by Georges on 12/01/2017.
 */

public class EventMoveMouse {
    double mouseX,mouseY;
    long eventId;

    public EventMoveMouse(long eventDate, double mouseX, double mouseY) {
        this.eventId = eventDate;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
    public EventMoveMouse() {

    }


    public void setMouseX(double mouseX) {
        this.mouseX = mouseX;
    }

    public void setMouseY(double mouseY) {
        this.mouseY = mouseY;
    }

    public void setEventlong(long eventDate) {
        this.eventId = eventDate;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public long getEventLong() {
        return eventId;
    }

}
