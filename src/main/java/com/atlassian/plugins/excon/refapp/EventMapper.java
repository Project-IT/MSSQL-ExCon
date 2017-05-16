package com.atlassian.plugins.excon.refapp;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import java.util.Date;

/**
 * Written by ExCon Group from KTH Sweden - Code is available freely at our Github
 * under the GNU GPL.
 *
 * Created on 2017-05-05.
 */

public class EventMapper {

    //global identifier for table name
    private final String tableName = "OutlookUIDtable";

    /**
     * @param myConn <- The connection from execute
     * @throws SQLException Creates a new table in the SQL database if there were none before.
     *                      Does nothing otherwise.
     *                      <p>
     *                      This table is used to map the Unique IDs that are retrieved from the Outlook events
     *                      to the events that will be inserted into Confluence - they also get Unique IDs based
     *                      on the format Confluence uses.
     */

    public void tableMaker(Connection myConn) throws SQLException {
        Statement stmt = myConn.createStatement();
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + "(ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, OutlookUID VARCHAR(255) , ConfluenceUID VARCHAR(2048), Username VARCHAR(255), CalendarID VARCHAR(255))";
        stmt.execute(sqlCreate);
    }

    /**
     * @param OutlookUID <- The Unique Identifier from the Outlook Event that is currently handled
     * @param user       <- The Outlook Username
     * @param myConn     <- The connection from execute
     * @param eu         <- The event updater used in execute
     * @param ep         <- The Event Parameters for the currently handled event
     * @return <- Returns true if event is known, false if it is a new event
     * @throws SQLException This function is called at the end of every for-loop in execute. It handles the information retrieved from
     *                      Outlook that is currently stored in the EventParameter class and uses that to insert the event into the
     *                      Confluence calender OR update an already known event.
     *                      <p>
     *                      This function takes care of quite a lot.
     *                      <p>
     *                      It checks if the username used to log in to Outlook is already known, if such - update and insert that users
     *                      events into the database. Else, map a new user into the table (only username is stored) and map events  under
     *                      that username. This is to prevent deletion later.
     */

    @SuppressWarnings("Duplicates")
    public boolean tableMap(String OutlookUID, String user, Connection myConn,
                            EventUpdater eu, eventParameters ep, String globalCalendar) throws SQLException {

        // Get current time
        SimpleDateFormat test = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z--'");
        test.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();

        // Create a random unique value (in the style of ConfluenceUIDs
        // for each event that is in the calendar of Outlook
        Random random = new Random();
        int value = random.nextInt(999999999) + 1000000000;
        String NewVEVUID = (test.format(date) + String.valueOf(value) + "@localhost"); //VEVENT UID (after "@" put the IP of the host)

        // Prepare for query
        eventInserter ei = new eventInserter();
        Statement stmt = myConn.createStatement();
        PreparedStatement preparedStatement;

        // Check if user is known
        PreparedStatement userStatement = myConn.prepareStatement("SELECT Username FROM confluence.outlookuidtable WHERE Username='" + user + "'");
        ResultSet userRs = userStatement.executeQuery();

        //prepare for adding which user is invited
        InviteesInserter Ii = new InviteesInserter();
        LastEventIdFinder Leif = new LastEventIdFinder();

        //user is known, update their events
        if (userRs.next()) {

            preparedStatement = myConn.prepareStatement("SELECT ConfluenceUID FROM confluence.outlookuidtable WHERE OutlookUID='" + OutlookUID + "'");
            ResultSet myRs = preparedStatement.executeQuery();

            // new event
            if (!myRs.next()) {
                String sqlInsert = "INSERT INTO " + tableName + "(OutlookUID, ConfluenceUID, Username, CalendarID)" + "VALUES ('" + OutlookUID + "', '" + NewVEVUID + "', '" + user + "', '" + globalCalendar + "')";
                ep.setVevent_uid(NewVEVUID);
                stmt.executeUpdate(sqlInsert);
                ei.insert(ep, myConn);
                Ii.insert(Leif.find(myConn), ep.getOrganiser(), myConn);
                return false;
            //old event
            } else {
                ep.setVevent_uid(myRs.getString("ConfluenceUID"));
                eu.update(ep, myConn);
            }
            return true;

        // user is not known, map uniquely to database
        } else {

            PreparedStatement newUserMap = myConn.prepareStatement("SELECT ConfluenceUID FROM confluence.outlookuidtable WHERE OutlookUID='" + OutlookUID + "'");
            ResultSet userMapRS = newUserMap.executeQuery();
            // Like before insert new events into Calendar under new username
            if (!userMapRS.next()) {
                String sqlInsert = "INSERT INTO " + tableName + "(OutlookUID, ConfluenceUID, Username, CalendarID)" + "VALUES ('" + OutlookUID + "', '" + NewVEVUID + "', '" + user + "', '" + globalCalendar + "')";
                ep.setVevent_uid(NewVEVUID);
                stmt.executeUpdate(sqlInsert);
                ei.insert(ep, myConn);
                Ii.insert(Leif.find(myConn), ep.getOrganiser(), myConn);
                return false;
            } else {
                ep.setVevent_uid(userMapRS.getString("ConfluenceUID"));
                eu.update(ep, myConn);
            }
            return true;
        }
    }
}
