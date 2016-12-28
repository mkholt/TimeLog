package com.t_hawk.timelog.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Project: TimeLog
 * Created by Morten on 12/28/2016.
 * @author Morten
 */

public class Task {
    private String name;

    private List<Task> subTasks;

    public Task(String name) {
        this.name = name;
        subTasks = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        return String.format("%1$s%2$s", name, (subTasks.size() > 0 ? " (" + subTasks.size() + ")": ""));
    }
}
