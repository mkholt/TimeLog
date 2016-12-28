package com.t_hawk.timelog.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.t_hawk.timelog.model.Task;

/**
 * Project: TimeLog
 * Created by Morten on 12/28/2016.
 * @author Morten
 */

public class TaskListAdapter extends ArrayAdapter<Task> {
    public TaskListAdapter(Context context, int resource, Task[] objects) {
        super(context, resource, objects);
    }
}
