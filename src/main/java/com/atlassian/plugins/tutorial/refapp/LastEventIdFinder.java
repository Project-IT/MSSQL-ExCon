package com.atlassian.plugins.tutorial.refapp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ExCon Group on 2017-05-11.
 */
public class LastEventIdFinder {

    protected String insertQuery="SELECT MAX(ID) ID FROM confluencebu.ao_950dc3_tc_events";
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
