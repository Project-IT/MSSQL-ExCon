package com.atlassian.tutorial.macro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by alexander on 2017-05-05.
 */
public class EventUpdater {

    PreparedStatement ps = null;

    public void update(eventParameters dbEventObject, Connection myConn) throws SQLException{

        ps = myConn.prepareStatement(dbEventObject.getUpdateQuery());
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
        //System.out.println(ps.toString());
        System.out.println(ps.toString());
        ps.executeUpdate();
    }
}
