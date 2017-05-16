package com.atlassian.plugins.excon.refapp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Written by ExCon Group from KTH Sweden - Code is available freely at our Github
 * under the GNU GPL.
 *
 * Created by on 2017-05-11.
 */
public class LastEventIdFinder {

    protected final String insertQuery="SELECT MAX(ID) ID FROM confluence.ao_950dc3_tc_events";
    public int find(Connection myConn)throws SQLException{
        int id=0;
        Statement myStm=myConn.createStatement();
        ResultSet myRs=myStm.executeQuery(insertQuery);
        if(myRs.next()){
            id=myRs.getInt("ID");
        }
        return id;
    }
}
