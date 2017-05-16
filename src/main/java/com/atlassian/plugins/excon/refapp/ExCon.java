package com.atlassian.plugins.excon.refapp;

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
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.util.Date;

/**
 * Written by ExCon Group from KTH Sweden - Code is available freely at our Github
 * under the GNU GPL.
 *
 * @param username     <- Retrieved from MyPluginServlet that gets it from Admin.vm
 * @param password     <- Retrieved from MyPluginServlet that gets it from Admin.vm
 * @param calendarName <- Retrieved from MyPluginServlet that gets it from Admin.vm
 * @throws ServletException <- Throws appropriate exception
 * <p>
 * The execute function takes the above mentioned parameters, handles the login to Outlook
 * and then sends the information retrieved from outlook into the EventParameter class.
 * That - in turn - handles the SQL query needed for inserting into the event table.
 * The biggest part of the execute function is the for-loop getting each event from outlook
 * It was benchmarked to run at ~nlog(n) time. Refer to our documentation for more details.
 * <p>
 * Within execute, there are a number of function calls. Some are separate classes while
 * others are nested below. Refer to function comments for more details.
 */

public class ExCon {


    public void execute(String username, String password, String calendarName, String url, String IPpass, String IPuser, int months) throws ServletException {

        //declarations
        String globalCalendar = "";
        String calID = null;

        //Acquires the current User
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();

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

        //Creates a Date up to 2 years from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, months);
        Date endDate = cal.getTime();

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
            findResults = cf.findAppointments(new CalendarView(new Date(), endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            findResults.getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        eventParameters ep = new eventParameters();
        Connection myConn;

        //sets database user
        try {
            ep.setUser(IPuser);
            ep.setPassword(IPpass);
            ep.setdbUrl(url);
            myConn = DriverManager.getConnection(ep.getDbUrl(), ep.getUser(), ep.getPassword());

            EventMapper em = new EventMapper();
            em.tableMaker(myConn);

            EventUpdater eu = new EventUpdater();
            EventDeleter ed = new EventDeleter();

            /**
             * This for loop is the biggest part of our program.
             * It runs at nlog(n) time.
             */

            for (Appointment appt : findResults.getItems()) {

                // Loads appt
                try {
                    appt.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Checks if allday


                if (appt.getIsAllDayEvent()) {

                    ep.setAll_day("1");

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

                    //Last modified
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

                    //rec. Id Timestamp
                    ep.setRecurrence_id_timestamp(0);
                    //Rec. Rule
                    ep.setRecurrence_rule(null);
                    //Reminder_SETTING_ID
                    ep.setReminder_setting_id(null);

                    //Sequence
                    ep.setSequence(appt.getAppointmentSequenceNumber().toString());

                    //SubCalID
                    calID = ParentID(calendarName, myConn);
                    ep.setSub_calendar_id(calID);
                    globalCalendar = calID;

                    //SUMMARY
                    ep.setSummary(appt.getSubject());
                    //URL
                    ep.setUrl(appt.getMeetingWorkspaceUrl());

                    //URL START
                    try {
                        ep.setUtc_start(ConvertTime(appt.getStart(), false));
                    } catch (ParseException x) {
                        x.printStackTrace();
                    }

                    //URL END
                    try {
                        ep.setUtc_end(ConvertTime(appt.getEnd(), false));
                    } catch (ParseException x) {
                        x.printStackTrace();
                    }

                    //If the event is of recurring type, make sure each part gets a unique id
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

                    //call tablemapper to map events in outlook to events in confluence
                    em.tableMap(ep.getVevent_uid(), username, myConn, eu, ep, globalCalendar);
                }
            }
            //clean up database
            ed.delete(username, myConn);
            //close connection
            myConn.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * @implements IAutodiscoverRedirectionUrl
     * <p>
     * Function provided by Exchange Web-Service which is distributed under the MIT License.
     * For more information refer to their user-manual.
     */

    static class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {
        public boolean autodiscoverRedirectionUrlValidationCallback(
                String redirectionUrl) {
            return redirectionUrl.toLowerCase().startsWith("https://");
        }
    }

    /**
     * @param date
     * @param days
     * @return Simple function for adding days in-case.
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * @param time      time & date of the event
     * @param localtime Determines whether or not the time is to be local or UTC
     * @Throws Exception
     * <p>
     * Converts the time acquired from the specific Outlook event to the compatible Unix Epoch time format.
     */
    private static String ConvertTime(Date time, boolean localtime) throws Exception {

        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        if (localtime) {
            try {
                date = df.parse(time.toString());
                date = addDays(date, 1);
            } catch (ParseException exc) {
                exc.printStackTrace();
            }
            return String.valueOf(date.getTime());
        } else {
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = df.parse(df.format(time));
                date = addDays(date, 1);
            } catch (ParseException exc) {
                exc.printStackTrace();
            }
            return String.valueOf(date.getTime());
        }
    }

    /**
     * @param CalendarName <-- The desired calendar name
     * @param myConn       <-- the connection to SQL server
     * @return resultID          <-- returns the desired Subcalendar_ID
     * <p>
     * ParentID is a function that makes a SQL query which retrieves a list with all
     * PARENT IDs and then retrieves the desired one based on CalendarName
     */
    private static String ParentID(String CalendarName, Connection myConn) throws Exception {

        Statement State = myConn.createStatement();
        String ID = null, resultID = null;
        ResultSet Res = State.executeQuery
                ("SELECT tc.NAME as calendar_name, tc.ID as ID FROM AO_950DC3_TC_SUBCALS tc JOIN user_mapping um ON um.user_key = tc.CREATOR WHERE tc.SPACE_KEY IS NOT NULL;");

        while (Res.next()) {
            if (Res.getString("calendar_name").equals(CalendarName)) {
                ID = Res.getString("ID");
            }
        }
        Res.close();
        ResultSet myRs = State.executeQuery("SELECT ID FROM confluence.ao_950dc3_tc_subcals WHERE PARENT_ID= '" + ID + "' AND COLOUR='subcalendar-blue';");
        if (myRs.next()) {
            resultID = myRs.getString("ID");

        }
        return resultID;
    }
}
