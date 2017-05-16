package com.atlassian.plugins.excon.refapp;

/**
 * Written by ExCon Group from KTH Sweden - Code is available freely at our Github
 * under the GNU GPL.
 *
 * Deprecated Class
 */
public class Event {

    String start;       // Start time of the event
    String end;         // End time of the event
    String location;    // Location of the event
    String body;        // Body of the event
    String allDay;     // Boolean to check whether the event is "all day" event or not
    String subject;     // Event subject

    public void addStart(String start) {
        this.start = start;
    }

    public void addEnd(String end) {
        this.end = end;
    }

    public void addLocation(String location) {
        this.location = location;
    }

    public void addBody(String body) {
        this.body = body;
    }

    public void addAllDayEvent(String allDay) {
        this.allDay = allDay;
    }

    public void addSubject(String subject) {
        this.subject = subject;
    }

    public String stringer() {
        return "| Subject = " + this.subject + "| Start =" + this.start +
                "|  End = " + this.end + "|  Location = " + this.location +
                "| isAllDay = " + this.allDay + "| Body = " + body + "  \n";
    }
}
