package com.t_hawk.timelog.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Project: TimeLog
 * Created by Morten on 12/28/2016.
 * @author Morten
 */

public class Task {
    private String _name;

    private List<Task> _subTasks;

    public Task(String name) {
        _name = name;
        _subTasks = new LinkedList<>();
    }

    @Override
    public String toString() {
        return String.format("%1$s%2$s", _name, (_subTasks.size() > 0 ? " (" + _subTasks.size() + ")": ""));
    }
}
