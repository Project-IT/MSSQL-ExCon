package com.atlassian.plugins.tutorial.refapp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ExCon Group on 2017-05-11.
 */
public class LastEventIdFinder {

    protected final String insertQuery="SELECT MAX(ID) ID FROM [confluence].[dbo].[AO_950DC3_TC_EVENTS]";
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
