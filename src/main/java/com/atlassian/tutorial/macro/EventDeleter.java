package com.atlassian.tutorial.macro;

import java.sql.*;
import java.util.ArrayList;


/**
 * Created by alexander on 2017-05-08.
 */
public class EventDeleter {

    ArrayList outlookIDs = new ArrayList();

    public void delete(Connection myConn) throws SQLException {

        PreparedStatement ps = myConn.prepareStatement("SELECT OutlookUID FROM confluence.outlookuidtable");
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
