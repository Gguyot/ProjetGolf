package com.example.georges.projectgolf;

import android.util.Log;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Georges on 12/01/2017.
 */

public class EventMoveMouse  implements Serializable, Comparable<EventMoveMouse>{
    float mouseX,mouseY;
    long eventId;
    double eventDate;



    public EventMoveMouse(int mouseX, int mouseY, long eventId, double eventDate) {
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

    public double getEventDate() {
        return eventDate;
    }

    public void setEventDate(double eventDate) {
        this.eventDate = eventDate;
    }


    @Override
    public int compareTo(EventMoveMouse objetC) {
        if (objetC.getMouseX()<this.mouseX)
        {
            return 1;
        }else
        {
            return -1;
        }
    }
}
