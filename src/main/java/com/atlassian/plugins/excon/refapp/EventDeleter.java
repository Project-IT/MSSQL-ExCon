package com.atlassian.plugins.excon.refapp;
import java.sql.*;
import java.util.ArrayList;

/**
 * Written by ExCon Group from KTH Sweden - Code is available freely at our Github
 * under the GNU GPL.
 *
 * Created on 2017-05-08.
 *
 */
public class EventDeleter {

    // Global ArrayList used by execute to gather known outlook IDs
    ArrayList outlookIDs = new ArrayList();

    /**
     *
     * @param user            <- Current Outlook username
     * @param myConn          <- Connection from execute
     * @throws SQLException
     *
     * This function is called after each for-loop in ExCon/execute. It handles database
     * cleanup such that events deleted in Outlook are also deleted in Confluence.
     *
     * This is the reason the OutlookUIDtable in the database needs the username field - otherwise
     * every new user would delete the previously added events.
     *
     * This function compares to different Lists:
     * One with known OutlookUIDs FROM outlook
     * One with known OutlookUIDs FROM the database
     *
     * Eg. It compares to see if the database is still storing a now deleted event if such - it deletes that
     * Event from both tables in the database.
     *
     */

    public void delete(String user, Connection myConn) throws SQLException {


        //prepare for inputting into ArrayList
        PreparedStatement ps = myConn.prepareStatement("SELECT OutlookUID FROM confluence.outlookuidtable WHERE Username='" + user+"'");

        ResultSet rs = ps.executeQuery();

        //declaration
        ArrayList tableIDs = new ArrayList();

        //store all known OutlookIDs from the database in this ArrayList
        while (rs.next()) {
            tableIDs.add(rs.getString(1));
        }

        // Compare IDs from Outlook to the Database table - remove if matching
        // this leaves the ArrayList tableIDs with only events that are primed
        // for deletion
        for (Object outlookID : outlookIDs) {
            for (int k = 0; k < tableIDs.size(); k++) {
                if (outlookID.equals(tableIDs.get(k))) {
                    tableIDs.remove(k);
                }
            }
        }

        //prepare statement for SQL queries
        Statement stmt = myConn.createStatement();


        //make sure ArrayList is not empty

        if (tableIDs.size() != 0) {
            //loop through remaining IDs - delete correct events
            for (Object ID : tableIDs) {

                //Prepare ResultSet from SQL for comparision
                PreparedStatement ps2 = myConn.prepareStatement("SELECT ConfluenceUID FROM confluence.outlookuidtable WHERE OutlookUID='" + ID + "'");
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {

                    //Compare RS to ArrayList - then - Delete from Confluence Event table
                    String calendarDel = "DELETE FROM confluence.ao_950dc3_tc_events WHERE VEVENT_UID='" + rs2.getString("ConfluenceUID") + "'";
                    stmt.executeUpdate(calendarDel);

                    //Compare to ArrayList - then - Delete from OutlookUID table
                    String sqlDel = "DELETE FROM confluence.outlookuidtable WHERE OutlookUID='" + ID + "'";
                    stmt.executeUpdate(sqlDel);
                }
            }
        }
    }
}