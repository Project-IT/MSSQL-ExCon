package com.atlassian.plugins.tutorial.refapp;

import java.sql.*;
import java.util.ArrayList;


/**
 * Created by alexander on 2017-05-08.
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

        PreparedStatement ps = myConn.prepareStatement("SELECT OutlookUID FROM confluence.outlookuidtable WHERE Username='" + user+"'");
        ResultSet rs = ps.executeQuery();

        ArrayList tableIDs = new ArrayList();

        while (rs.next()) {
            tableIDs.add(rs.getString(1));
        }

        //Compare IDs from Outlook to the Database table - remove if matching
        for (Object outlookID : outlookIDs) {
            for (int k = 0; k < tableIDs.size(); k++) {
                if (outlookID.equals(tableIDs.get(k))) {
                    tableIDs.remove(k);
                }
            }
        }

        Statement stmt = myConn.createStatement();

        //loop through remaining IDs - delete correct events
        if (tableIDs.size() != 0) {
            for (Object ID : tableIDs) {

                PreparedStatement ps2 = myConn.prepareStatement("SELECT ConfluenceUID FROM confluence.outlookuidtable WHERE OutlookUID='" + ID + "'");
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {

                    String calendarDel = "DELETE FROM confluence.ao_950dc3_tc_events WHERE VEVENT_UID='" + rs2.getString("ConfluenceUID") + "'";
                    stmt.executeUpdate(calendarDel);
                    String sqlDel = "DELETE FROM confluence.outlookuidtable WHERE OutlookUID='" + ID + "'";
                    stmt.executeUpdate(sqlDel);

                }
            }
        }
    }
}
