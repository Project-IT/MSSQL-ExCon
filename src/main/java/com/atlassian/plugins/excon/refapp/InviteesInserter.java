package com.atlassian.plugins.excon.refapp;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Written by ExCon Group from KTH Sweden - Code is available freely at our Github
 * under the GNU GPL.
 *
 * Created by on 2017-05-11.
 */
public class InviteesInserter {

    //declaration of database table name
    protected final String TABLENAME="confluence.ao_950dc3_tc_events_invitees";

    //preparing for SQL query
    protected String insertQuery="INSERT INTO " + TABLENAME +
            " (EVENT_ID, INVITEE_ID)" +
            " VALUES (?, ?)";

    protected PreparedStatement ps=null;

    /**
     * @param event_Id      This needs to correspond to the ID of one entry in the confluence.ao_950dc3_tc_events db-table
     * @param invitee_Id    This needs to correspond to the user key of the organiser field in confluence.ao_950dc3_tc_events
     *                      The user key is stored in the db-table named user_mapping
     * @param myConn        The connection used to communicate with the database
     *
     * Inserts all the invitees that are invited to a specific event.
     * You cannot move or edit the event from the confluence calendar if invitees is not specified.
     */
    public void insert(int event_Id, String invitee_Id, Connection myConn){
        try {
            ps=myConn.prepareStatement(insertQuery);
            ps.setInt(1,  event_Id);
            ps.setString(2,  invitee_Id);
            ps.executeUpdate();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
