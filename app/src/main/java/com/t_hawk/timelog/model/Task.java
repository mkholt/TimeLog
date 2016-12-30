package com.t_hawk.timelog.model;

import com.orm.SugarRecord;

import java.util.LinkedList;
import java.util.List;

/**
 * Project: TimeLog
 * Created by Morten on 12/28/2016.
 * @author Morten
 */

public class Task extends SugarRecord {
    private String name;

    private Task parentTask;

    public Task() {
        this("Unnamed task");
    }

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public List<Task> getSubTasks() {
        return Task.find(Task.class, "parent_task = ?", String.valueOf(getId()));
    }

    public long numberOfSubTasks() {
        return Task.count(Task.class, "parent_task = ?", new String[] { String.valueOf(getId()) });
    }

    public List<Registration> getRegistrations() {
        return Registration.find(Registration.class, "task = ?", String.valueOf(getId()));
    }

    public long getDuration() {
        long duration = 0;

        for (Registration r : getRegistrations()) {
            duration += r.getDuration();
        }

        return duration;
    }

    @Override
    public String toString() {
        long numberOfSubTasks = numberOfSubTasks();
        return String.format("%1$s%2$s", name, (numberOfSubTasks > 0 ? " (" + numberOfSubTasks + ")": ""));
    }
}
