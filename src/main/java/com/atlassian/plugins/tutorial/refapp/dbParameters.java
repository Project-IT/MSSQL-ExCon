package com.atlassian.plugins.tutorial.refapp;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * ConfluenceCreated by jeppe on 2017-04-29.
 */
public abstract class dbParameters {
    protected String DbDriver="jdbc:mysql://";

    protected String dbUrl=     null;
    protected String user=      null;
    protected String password=  null;

    public boolean verifyDbParameters(){
        if(dbUrl==null)                  return false;
        if(user==null)                   return false;
        if(password==null)               return false;
        //Testing connection
        try{
            Connection myConn = DriverManager.getConnection(dbUrl, user, password);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void setDbDriver(String s){
        this.DbDriver=s;
    }
    public void setdbUrl(String s){
        this.dbUrl=DbDriver + s;
    }
    public void setPassword(String s){
        this.password=s;
    }
    public void setUser(String s){
        this.user=s;
    }

    public String getDbDriver(){
        return DbDriver;
    }
    public String getDbUrl(){
        return dbUrl;
    }
    public String getPassword(){
        return password;
    }
    public String getUser(){
        return user;
    }

}
