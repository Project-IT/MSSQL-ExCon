package com.atlassian.plugins.tutorial.refapp;
import java.sql.*;
/**
 * ConfluenceCreated by jeppe on 2017-04-28.
 */
public class eventInserter {
    protected PreparedStatement ps=null;
    public void insert(eventParameters dbEventObject, Connection myConn) {
        if (dbEventObject.verifyParameters()) {
            try {
                    Statement myStm = myConn.createStatement();
                    ps = myConn.prepareStatement(dbEventObject.getInsertQuery());
                    ps.setString(1,  dbEventObject.getAll_day());
                    ps.setString(2,  dbEventObject.getCreated());
                    ps.setString(3,  dbEventObject.getDescription());
                    ps.setString(4,  dbEventObject.getEnd());
                    ps.setString(5,  dbEventObject.getLast_modified());
                    ps.setString(6,  dbEventObject.getLocation());
                    ps.setString(7,  dbEventObject.getOrganiser());
                    ps.setInt   (8,  dbEventObject.getRecurrence_id_timestamp());
                    ps.setString(9,  dbEventObject.getRecurrence_rule());
                    ps.setString(10, dbEventObject.getReminder_setting_id());
                    ps.setString(11, dbEventObject.getSequence());
                    ps.setString(12, dbEventObject.getStart());
                    ps.setString(13, dbEventObject.getSub_calendar_id());
                    ps.setString(14, dbEventObject.getSummary());
                    ps.setString(15, dbEventObject.getUrl());
                    ps.setString(16, dbEventObject.getUtc_end());
                    ps.setString(17, dbEventObject.getUtc_start());
                    ps.setString(18, dbEventObject.getVevent_uid());
                    System.out.println(ps.toString());
                    ps.executeUpdate();
                } catch (Exception exc) {
                    exc.printStackTrace();
            }
        }
    }
}
