package com.example.collegealertapp;


public class Event {
    public long   id;
    public String title;
    public String description;
    public long   eventTime;

    public Event(long id, String title, String desc, long eventTime) {
        this.id = id;
        this.title = title;
        this.description = desc;
        this.eventTime = eventTime;
    }
}
