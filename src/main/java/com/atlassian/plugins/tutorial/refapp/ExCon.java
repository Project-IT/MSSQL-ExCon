package com.atlassian.plugins.tutorial.refapp;

import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.util.Date;

public class ExCon {

    public void execute(String username, String password, String calendarName, String url, String IPpass, String IPuser) throws ServletException {

        String fromOutlook = "";
        String vacationID;

        ConfluenceUser currentUser;
        currentUser = AuthenticatedUserThreadLocal.get();

        // Specifies Exchange version, (any newer works as well)
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        // Log in with the respective Exchange account
        ExchangeCredentials credentials = new WebCredentials(username, password);
        // Verifies the credentials
        service.setCredentials(credentials);

        // Finds the URI for the E-mail
        try {
            service.autodiscoverUrl(username, new RedirectionUrlCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sets the Date Format
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Sets start Date
        Date startDate = null;
        try {
            startDate = formatter.parse("2017-04-10 12:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Sets end Date
        Date endDate = null;
        try {
            endDate = formatter.parse("2017-05-30 13:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Defines which Calendar Folder to use
        CalendarFolder cf = null;
        try {
            cf = CalendarFolder.bind(service, WellKnownFolderName.Calendar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Makes an array of Calendar Results
        FindItemsResults<Appointment> findResults = null;
        try {
            findResults = cf.findAppointments(new CalendarView(startDate, endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        findResults.getItems();

        eventParameters ep = new eventParameters();
        Connection myConn;

        try {
            ep.setUser(IPuser);
            ep.setPassword(IPpass);
            ep.setdbUrl(url);
            myConn = DriverManager.getConnection(ep.getDbUrl(), ep.getUser(), ep.getPassword());

            EventMapper em = new EventMapper();
            em.tableMaker(myConn);

            EventUpdater eu = new EventUpdater();
            EventDeleter ed = new EventDeleter();

            for (Appointment appt : findResults.getItems()) {

                // Loads appt
                try {
                    appt.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Sets allday
                try {
                    if (appt.getIsAllDayEvent()) {
                        ep.setAll_day("1");
                    } else {
                        ep.setAll_day("0");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //Time created
                try {
                    ep.setCreated(ConvertTime(appt.getDateTimeCreated(), true));   //created
                } catch (ParseException x) {
                    x.printStackTrace();
                }

                //set description
                try {
                    Document doc = Jsoup.parse(appt.getBody().toString());
                    ep.setDescription(doc.body().text());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //START
                try {
                    ep.setStart(ConvertTime(appt.getStart(), true));
                } catch (ParseException x) {
                    x.printStackTrace();
                }

                //End time
                try {
                    ep.setEnd(ConvertTime(appt.getEnd(), true));
                } catch (ParseException x) {
                    x.printStackTrace();
                }

                //Last_Modified
                try {
                    ep.setLast_modified(ConvertTime(appt.getLastModifiedTime(), true));
                } catch (ParseException x) {
                    x.printStackTrace();
                }


                //Location
                try {
                    if (appt.getLocation() != null)
                        ep.setLocation(appt.getLocation());
                    else ep.setLocation("");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Sets Organiser
                try {
                    String wholeUser = currentUser.toString();
                    String[] split = wholeUser.split("key");
                    wholeUser = split[1];
                    String UserKey = wholeUser.replaceAll("=|}", "");
                    ep.setOrganiser(UserKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ep.setRecurrence_id_timestamp(0);        //rec. Id Timestamp
                ep.setRecurrence_rule(null);               //Rec. Rule
                ep.setReminder_setting_id(null);           //Reminder_SETTING_ID


                //Sequence
                ep.setSequence(appt.getAppointmentSequenceNumber().toString());

                //SUB_CALENDAR_ID
                //this is for different vacation event type
                if (appt.getCategories().toString().equals("Orange category,")) // for Vacation events
                {
                    vacationID = SubCalendarID(ParentID(calendarName, myConn), myConn, "Orange");
                } else {
                    vacationID = SubCalendarID(ParentID(calendarName, myConn), myConn, "Blue");
                }

                ep.setSub_calendar_id(vacationID);
                ep.setSummary(fromOutlook);                //SUMMARY
                ep.setUrl(appt.getMeetingWorkspaceUrl());           //URL

                //Subject
                ep.setSummary(appt.getSubject());

                //URL
                ep.setUrl(appt.getMeetingWorkspaceUrl());

                //URL END
                try {
                    ep.setUtc_end(ConvertTime(appt.getEnd(), false));
                } catch (ParseException x) {
                    x.printStackTrace();
                }

                //URL START
                try {
                    ep.setUtc_start(ConvertTime(appt.getStart(), false));
                } catch (ParseException x) {
                    x.printStackTrace();
                }

                //work here
                if (appt.getIsRecurring()) {
                    Random random = new Random();
                    int value = random.nextInt(999999999) + 1000000000;
                    String c = String.valueOf(value);
                    ep.setVevent_uid(c);
                    ed.outlookIDs.add(c);
                } else {
                    ep.setVevent_uid(appt.getICalUid());
                    ed.outlookIDs.add(appt.getICalUid());
                }
                // stop working

                em.tableMap(ep.getVevent_uid(), username, myConn, eu, ep);
            }
            ed.delete(username, myConn); //clean up database
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    // Simple error checker for the URI
    static class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {
        public boolean autodiscoverRedirectionUrlValidationCallback(
                String redirectionUrl) {
            return redirectionUrl.toLowerCase().startsWith("https://");
        }

    }

    /* Converts the time acquired from the specific Outlook event to the compatible Unix Epoch time format.
       time -> time & date of the event
       localtime -> Determines whether or not the time is to be local or UTC
    */
    private static String ConvertTime(Date time, boolean localtime) throws Exception {

        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        if (localtime) {
            try {
                date = df.parse(time.toString());
            } catch (ParseException exc) {
                exc.printStackTrace();
            }
            return String.valueOf(date.getTime());
        } else {
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = df.parse(df.format(time));
            } catch (ParseException exc) {
                exc.printStackTrace();
            }
            return String.valueOf(date.getTime());
        }
    }

    private String SubCalendarID(String parentID, Connection myConn, String color) throws SQLException {
        String resultID = "";
        ResultSet myRs;
        //Create a statement
        Statement myStm = myConn.createStatement();
        // Get the child-ID of the parentID to corresponding color
        if (color.equals("Orange")) {
            myRs = myStm.executeQuery("SELECT ID FROM confluence.ao_950dc3_tc_subcals WHERE PARENT_ID= '" + parentID + "' AND COLOUR='subcalendar-orange';");
            if (myRs.next()) {
                resultID = myRs.getString("ID");
                return resultID;
            }
        } else {
            myRs = myStm.executeQuery("SELECT ID FROM confluence.ao_950dc3_tc_subcals WHERE PARENT_ID= '" + parentID + "' AND COLOUR='subcalendar-blue';");
            if (myRs.next()) {
                resultID = myRs.getString("ID");
                return resultID;
            }
        }
        return resultID;
    }

    /**
     * ParentID is a function that makes a SQL query which retrieves a list with all PARENT IDs and then retreives the desired one based on CalendarName
     *
     * @param CalendarName <-- The desired calendar name
     * @param myConn       <-- the connection to SQL server
     * @return ID <-- returns the desired Subcalendar_ID
     */
    private static String ParentID(String CalendarName, Connection myConn) throws Exception {

        Statement State = myConn.createStatement();
        String ID = null;
        ResultSet Res = State.executeQuery
                ("SELECT tc.NAME as calendar_name, tc.ID as ID FROM AO_950DC3_TC_SUBCALS tc JOIN user_mapping um ON um.user_key = tc.CREATOR WHERE tc.SPACE_KEY IS NOT NULL;");

        while (Res.next()) {
            if (Res.getString("calendar_name").equals(CalendarName)) {
                ID = Res.getString("ID");
            }
        }
        return ID;
    }
}
