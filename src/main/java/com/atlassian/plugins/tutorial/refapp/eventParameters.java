package com.atlassian.plugins.tutorial.refapp;
import java.sql.*;

/**
 * ConfluenceCreated by jeppe on 2017-04-28.
 */
public class eventParameters extends dbParameters{
    protected final String TABLENAME="confluence.ao_950dc3_tc_events";

    protected String insertQuery="INSERT INTO " + TABLENAME +
            " (ALL_DAY, CREATED, DESCRIPTION, END, LAST_MODIFIED, LOCATION, ORGANISER, RECURRENCE_ID_TIMESTAMP, RECURRENCE_RULE, REMINDER_SETTING_ID, SEQUENCE, START, SUB_CALENDAR_ID, SUMMARY, URL, UTC_END, UTC_START, VEVENT_UID)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    protected String all_day=                null;
    protected String created=                null;
    protected String description=            null;
    protected String end=                    null;
    protected String last_modified=          null;
    protected String location=               null;
    protected String organiser=              null;
    protected int    recurrence_id_timestamp=0;
    protected String recurrence_rule=        null;
    protected String reminder_setting_id=    null;
    protected String sequence=               null;
    protected String start=                  null;
    protected String sub_calendar_id=        null;
    protected String summary=                null;
    protected String url=                    null;
    protected String utc_end=                null;
    protected String utc_start=              null;
    protected String vevent_uid=             null;

    //defensive method to check the input. What do we need here?
    public boolean verifyParameters(){
        EventVerifier ev=new EventVerifier();
        if(!verifyDbParameters())               return false;
        if(!ev.verifyAll_day(getAll_day()))        return false;
        if(!ev.verifyCreated(getCreated()))             return false;
        if(!ev.verifyDescription(getDescription()))         return false;
        if(!ev.verifyEnd(getEnd()))              return false;
        if(!ev.verifyLast_modified(getLast_modified()))       return false;
        if(!ev.verifyLocation(getLocation()))            return false;
        if(!ev.verifyOrganiser(getOrganiser()))           return false;
        //if(ev.verifyRecurrence_id_timestamp==1337)return false;
        if(!ev.verifyRecurrence_rule(getRecurrence_rule()))     return false;
        if(!ev.verifyReminding_setting_id(getReminder_setting_id()))return false;
        if(!ev.verifySequence(getSequence()))            return false;
        if(!ev.verifyStart(getStart()))              return false;
        if(!ev.verifySub_calendar_id(getSub_calendar_id()))     return false;
        if(!ev.verifySummary(getSummary()))             return false;
        if(!ev.verifyUrl(getUrl()))                 return false;
        if(!ev.verifyUtc_end(getUtc_end()))             return false;
        if(!ev.verifyUtc_start(getUtc_start()))           return false;
        if(!ev.verifyVevent_uid(getVevent_uid()))          return true;
        return true;
    }


    public void setAll_day(String s){
        this.all_day=s;
    }
    public void setCreated(String s){
        this.created=s;
    }
    public void setDescription(String s){
        this.description=s;
    }
    public void setEnd(String s){
        this.end=s;
    }
    public void setLast_modified(String s){
        this.last_modified=s;
    }
    public void setLocation(String s){
        this.location=s;
    }
    public void setOrganiser(String s){
        this.organiser=s;
    }
    public void setRecurrence_id_timestamp(int i){
        this.recurrence_id_timestamp=i;
    }
    public void setRecurrence_rule(String s){
        this.recurrence_rule=s;
    }
    public void setReminder_setting_id(String s){
        this.reminder_setting_id=s;
    }
    public void setSequence(String s){
        this.sequence=s;
    }
    public void setStart(String s){
        this.start=s;
    }
    public void setSub_calendar_id(String s){
        this.sub_calendar_id=s;
    }
    public void setSummary(String s){
        this.summary=s;
    }
    public void setUrl(String s){
        this.url=s;
    }
    public void setUtc_end(String s){
        this.utc_end=s;
    }
    public void setUtc_start(String s){
        this.utc_start=s;
    }
    public void setVevent_uid(String s){
        this.vevent_uid=s;
    }

    public String getInsertQuery(){
        return insertQuery;
    }
    public String getAll_day(){
        return all_day;
    }
    public String getCreated(){
        return created;
    }
    public String getDescription(){
        return description;
    }
    public String getEnd(){
        return end;
    }
    public String getLast_modified(){
        return last_modified;
    }
    public String getLocation(){
        return location;
    }
    public String getOrganiser(){ return organiser; }
    public int getRecurrence_id_timestamp(){
        return recurrence_id_timestamp;
    }
    public String getRecurrence_rule(){
        return recurrence_rule;
    }
    public String getReminder_setting_id(){
        return reminder_setting_id;
    }
    public String getSequence(){
        return sequence;
    }
    public String getStart(){
        return start;
    }
    public String getSub_calendar_id(){
        return sub_calendar_id;
    }
    public String getSummary(){
        return summary;
    }
    public String getUrl(){
        return url;
    }
    public String getUtc_end(){
        return utc_end;
    }
    public String getUtc_start(){
        return utc_start;
    }
    public String getVevent_uid(){
        return vevent_uid;
    }
}
