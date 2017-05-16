package com.atlassian.plugins.excon.refapp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Written by ExCon Group from KTH Sweden - Code is available freely at our Github
 * under the GNU GPL.
 *
 * Created by on 2017-05-05.
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
        ps.setNull  (8,  Types.BIGINT);
        ps.setNull  (9,  Types.VARCHAR);
        ps.setNull  (10, Types.VARCHAR);
        ps.setString(11, dbEventObject.getSequence());
        ps.setString(12, dbEventObject.getStart());
        ps.setString(13, dbEventObject.getSub_calendar_id());
        ps.setString(14, dbEventObject.getSummary());
        ps.setNull  (15, Types.BLOB);
        ps.setString(16, dbEventObject.getUtc_end());
        ps.setString(17, dbEventObject.getUtc_start());
        ps.setString(18, dbEventObject.getVevent_uid());
        ps.executeUpdate();
    }
}
