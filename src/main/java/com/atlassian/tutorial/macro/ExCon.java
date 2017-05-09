package com.atlassian.tutorial.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

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

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.util.Date;


public class ExCon implements Macro {

    public String execute(Map<String, String> map, String s, ConversionContext conversionContext) throws MacroExecutionException {

        String fromOutlook = "";
        String username = map.get("Username");
        String password = map.get("Password");


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

        //LinkedList<Event> eventsList = new LinkedList<Event>();

        eventParameters ep = new eventParameters();
        Connection myConn;

        try {
            ep.setUser("tcomkproj2017");
            ep.setPassword("tcomkproj2017");
            ep.setdbUrl("localhost:3306/confluence");
            myConn = DriverManager.getConnection(ep.getDbUrl(), ep.getUser(), ep.getPassword());
            //Connection myTestConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/datatest", ep.getUser(), ep.getPassword());
            EventMapper em = new EventMapper();
            em.tableMaker(myConn);
            EventUpdater eu = new EventUpdater();
            EventDeleter ed = new EventDeleter();

            for (Appointment appt : findResults.getItems()) {
                // Make a new Event object to hold data of one appointment
                // Loads appt
                try {
                    appt.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fromOutlook = appt.getSubject();

                ep.setAll_day("0");                //all day 1
                try {
                    ep.setCreated(ConvertTime(appt.getDateTimeCreated(), true));   //created
                } catch (ParseException x) {
                    x.printStackTrace();
                }
                ep.setDescription("");                //description
                try {
                    ep.setEnd(ConvertTime(appt.getEnd(), true));   //End
                } catch (ParseException x) {
                    x.printStackTrace();
                }
                try {
                    ep.setLast_modified(ConvertTime(appt.getLastModifiedTime(), true));   //Last_Modified
                } catch (ParseException x) {
                    x.printStackTrace();
                }
                ep.setLocation("");      //Location
                ep.setOrganiser("4028b8815babae10015babb056780000");//Organiser
                ep.setRecurrence_id_timestamp(0);            //rec. Id Timestamp
                ep.setRecurrence_rule("");            //Rec. Rule
                ep.setReminder_setting_id("");           //Reminder_SETTING_ID
                ep.setSequence("0");              //SEQUENCE
                try {
                    ep.setStart(ConvertTime(appt.getStart(), true));  //START
                } catch (ParseException x) {
                    x.printStackTrace();
                }
                ep.setSub_calendar_id("dfa1eb25-ef12-42c8-abcf-71dec96b58ac");//SUB_CALENDAR_ID
                ep.setSummary(fromOutlook);                //SUMMARY
                ep.setUrl("NULL");           //URL
                try {
                    ep.setUtc_end(ConvertTime(appt.getStart(), false));  //UTC_END
                    ep.setUtc_start(ConvertTime(appt.getStart(), false));  //UTC_START
                } catch (ParseException x) {
                    x.printStackTrace();
                }
                try {
                    ep.setUtc_start(ConvertTime(appt.getStart(), false));  //UTC_START
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

                em.tableMap(ep.getVevent_uid(), myConn, eu, ep);
            }
            ed.delete(myConn); //clean up database
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return fromOutlook;
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


    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }
}
