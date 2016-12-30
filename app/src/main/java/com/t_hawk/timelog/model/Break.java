package com.t_hawk.timelog.model;

import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;

/**
 * Project: TimeLog
 * Created by Morten on 12/29/2016.
 *
 * @author Morten
 */

public class Break extends SugarRecord {
    private Date startTime;

    private Date endTime;

    private Registration registration;

    private boolean paid;

    public Break()
    {
        this(null);
    }

    public Break(Registration registration)
    {
        this(Calendar.getInstance().getTime(), null, registration, false);
    }

    public Break(Date startTime, Date endTime, Registration registration, boolean paid) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.registration = registration;
        this.paid = paid;
    }

    public long getDuration() {
        return endTime.getTime() - startTime.getTime();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
