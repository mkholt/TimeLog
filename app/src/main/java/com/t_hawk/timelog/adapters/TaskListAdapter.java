package com.t_hawk.timelog.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.t_hawk.timelog.R;
import com.t_hawk.timelog.model.Task;

/**
 * Project: TimeLog
 * Created by Morten on 12/28/2016.
 * @author Morten
 */

public class TaskListAdapter extends ArrayAdapter<Task> {
    private final int _resource;
    private final Task[] _tasks;

    public TaskListAdapter(Context context, int resource, Task[] tasks) {
        super(context, resource, tasks);
        this._resource = resource;
        this._tasks = tasks;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        TaskHolder holder;

        if (null == row)
        {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(_resource, parent, false);

            holder = new TaskHolder();
            holder.Name = (TextView) row.findViewById(R.id.tasklist_task_name);
            holder.SubTasks = (TextView) row.findViewById(R.id.tasklist_task_subtasks);
            holder.TotalTime = (TextView) row.findViewById(R.id.tasklist_task_totaltime);

            row.setTag(holder);
        }
        else
        {
            holder = (TaskHolder)row.getTag();
        }

        Task task = _tasks[position];
        int subtaskCount = task.getSubTasks().size();

        holder.Name.setText(task.getName());
        holder.SubTasks.setText(subtaskCount == 1 ? "1 subtask" : subtaskCount + " subtasks");
        holder.TotalTime.setText(R.string.task_totaltime_dummy);

        return row;
    }

    private static class TaskHolder
    {
        TextView Name;
        TextView SubTasks;
        TextView TotalTime;
    }
}
