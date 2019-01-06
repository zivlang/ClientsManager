package com.example.clientsmanager.model;

public class Event {

    private String eventTimeAndDate;
    private String eventDesc;
    private String eventTtl;
    private static Event currentEvent;

    public Event(){}

    public Event(String newTtl, String newTimeAndDateString, String newDesc) {
        this.eventTtl = newTtl;
        this.eventTimeAndDate = newTimeAndDateString;
        this.eventDesc = newDesc;
    }

    public static void setCurrentEvent(Event currentEvent) {
        Event.currentEvent = currentEvent;
    }

    public static Event getCurrentEvent() {
        return currentEvent;
    }

    public String getEventTimeAndDate() {
        return eventTimeAndDate;
    }

    public void setEventTimeAndDate(String eventTimeAndDate) {
        this.eventTimeAndDate = eventTimeAndDate;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventTtl() {
        return eventTtl;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventTimeAndDate='" + eventTimeAndDate + '\'' +
                ", eventDesc='" + eventDesc + '\'' +
                ", eventTtl='" + eventTtl + '\'' +
                '}';
    }
}