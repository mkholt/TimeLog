package com.t_hawk.timelog.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.t_hawk.timelog.R;
import com.t_hawk.timelog.fragments.TaskListFragment;
import com.t_hawk.timelog.model.Task;

import java.util.List;

/**
 * Project: TimeLog
 * Created by Morten on 12/28/2016.
 * @author Morten
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private final Context context;
    private final List<Task> tasks;
    private final TaskListFragment.OnListFragmentInteractionListener listener;

    public TaskListAdapter(Context context, List<Task> tasks, TaskListFragment.OnListFragmentInteractionListener listener) {
        this.context = context;
        this.tasks = tasks;
        this.listener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        long subTaskCount = task.numberOfSubTasks();
        Resources res = context.getResources();
        int formatStringRes = subTaskCount == 1 ? R.string.task_subtask_count_singular : R.string.task_subtask_count_plural;

        holder.Item = tasks.get(position);
        holder.NameView.setText(task.getName());
        holder.SubTasksView.setText(String.format(res.getString(formatStringRes), subTaskCount));
        holder.TotalTimeView.setText(DateUtils.formatElapsedTime(task.getDuration() / 1000));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.Item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder
    {
        public final View view;
        Task Item;

        final TextView NameView;
        final TextView SubTasksView;
        final TextView TotalTimeView;

        TaskViewHolder(View view) {
            super(view);
            this.view = view;
            NameView = (TextView) view.findViewById(R.id.tasklist_task_name);
            SubTasksView = (TextView) view.findViewById(R.id.tasklist_task_subtasks);
            TotalTimeView = (TextView) view.findViewById(R.id.tasklist_task_totaltime);
        }
    }
}
