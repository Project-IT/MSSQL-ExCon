package com.atlassian.plugins.tutorial.refapp;
/**
 * Created by jeppe on 2017-05-01.
 */
public class EventVerifier {
    //defensive methods to check the input of the fields

    public boolean verifyAll_day(String all_day) {
        if (all_day.equals("0") || all_day.equals("1")) {
            return true;
        }

        return false;
    }

    private boolean verifyDate(String date) {
        if (date.length() == 13) {
            return true;
        }

        return false;
    }

    public boolean verifyCreated(String created) {
        if (verifyDate(created)) {
            return true;
        }
        return false;
    }

    public boolean verifyDescription(String description) {

        if (description == null) {
            return false;
        }
        return true;
    }

    public boolean verifyEnd(String end) {
        if (verifyDate(end)) {
            return true;
        }
        return false;
    }

    public boolean verifyLast_modified(String last_modified) {
        if (verifyDate(last_modified)) {
            return true;
        }
        return false;
    }


    public boolean verifyLocation(String location) {

        if (location == null) {
            return false;
        }
        return true;
    }

    public boolean verifyStart(String start) {
        if (verifyDate(start)) {
            return true;
        }
        return false;
    }

    public boolean verifyOrganiser(String organiser) {

        if (organiser.length() == 32) {
            return true;
        } else {
            return false;
        }

    }

    public boolean verifyRecurrence_rule(String recurrence_rule) {

        return true;
    }

    public boolean verifyRecurrence_id_timestamp(int recurrence_id_timestamp) {
        return true;
    }

    public boolean verifyReminding_setting_id(String reminding_setting_id) {
        return true;
    }

    public boolean verifySequence(String sequence) {
        return true;
    }

    public boolean verifySub_calendar_id(String sub_calendar_id) {

        if (sub_calendar_id.length() == 36) {
            return true;
        }

        return false;

    }

    public boolean verifySummary(String summary) {
        if (summary == null) {
            return false;
        }
        return true;
    }


    public boolean verifyUrl(String url) {


        return true;
    }

    public boolean verifyUtc_end(String utc_end) {
        if (verifyDate(utc_end)) {
            return true;
        }
        return false;

    }

    public boolean verifyUtc_start(String utc_start) {

        if (verifyDate(utc_start)) {
            return true;
        }
        return false;
    }

    public boolean verifyVevent_uid(String vevent_uid) {
        if (vevent_uid.length() == 38) {
            return true;
        }
        return false;
    }
}
