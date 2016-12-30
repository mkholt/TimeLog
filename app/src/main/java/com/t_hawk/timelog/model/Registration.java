package com.t_hawk.timelog.model;

import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Project: TimeLog
 * Created by Morten on 12/29/2016.
 *
 * @author Morten
 */

public class Registration extends SugarRecord {
    private Date startTime;

    private Date endTime;

    private Task task;

    public Registration()
    {
        this(null);
    }

    public Registration(Task task) {
        this(new Date(), null, task);
    }

    public Registration(Date startTime, Date endTime, Task task) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.task = task;
    }

    public List<Break> getBreaks() {
        return Break.find(Break.class, "registration = ?", String.valueOf(getId()));
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public long getDuration()
    {
        long complete = endTime.getTime() - startTime.getTime();
        long breaks = 0;
        for(Break b : getBreaks())
        {
            breaks += b.getDuration();
        }

        return complete - breaks;
    }
}
