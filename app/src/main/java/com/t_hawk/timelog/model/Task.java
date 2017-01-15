package com.t_hawk.timelog.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.util.LinkedList;
import java.util.List;

/**
 * Project: TimeLog
 * Created by Morten on 12/28/2016.
 * @author Morten
 */

public class Task extends SugarRecord implements Parcelable {
    private String name;

    private Task parentTask;

    public Task() {
        this("Unnamed task");
    }

    public Task(String name) {
        this.name = name;
    }

    private Task(Parcel in) {
        name = in.readString();
        parentTask = in.readParcelable(Task.class.getClassLoader());
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(parentTask, 0);
    }
}
