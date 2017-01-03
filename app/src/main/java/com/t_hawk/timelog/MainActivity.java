package com.t_hawk.timelog;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.orm.SugarDb;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.t_hawk.timelog.adapters.TaskListAdapter;
import com.t_hawk.timelog.model.Break;
import com.t_hawk.timelog.model.Registration;
import com.t_hawk.timelog.model.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Task> _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (null != fab) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        BuildDummyData();

        // TODO: This needs to be selectable in the UI
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DAY_OF_YEAR, -7);

        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_YEAR, -1);

        List<Registration> registrations = Select.from(Registration.class).and(
                Condition.prop("start_time").gt(from.getTimeInMillis()),
                Condition.prop("end_time").lt(to.getTimeInMillis())
        ).list();

        HashSet<Long> seen = new HashSet<>();
        ArrayList<Task> tasks = new ArrayList<>();
        for (Registration r : registrations)
        {
            Task t = r.getTask();
            if (t == null) continue;
            while (t.getParentTask() != null)
            {
                t = t.getParentTask();
            }

            if (!seen.contains(t.getId()))
            {
                seen.add(t.getId());
                tasks.add(t);
            }
        }

        _adapter = new TaskListAdapter(this, R.layout.content_main_task, tasks.toArray(new Task[0]));
        ListView listView = (ListView) findViewById(R.id.entries);

        if (null != listView) {
            listView.setAdapter(_adapter);
        }
    }

    private void BuildDummyData() {
        SugarDb sugarDb = new SugarDb(getApplicationContext());
        new File(sugarDb.getDB().getPath()).delete();
        Task.findById(Task.class, 1);
        Registration.findById(Registration.class, 1);
        Break.findById(Break.class, 1);

        Random rand = new Random();

        LongSparseArray<Task> taskMap = new LongSparseArray<>();
        for (int i = 0; i < 10; i++)
        {
            Task task = new Task("Task " + (i+1));
            task.save();
            taskMap.put(task.getId(), task);

            int sub = rand.nextInt(5);
            for (int j = 0; j < sub; j++)
            {
                Task subTask = new Task(task.getName() + "." + (j+1));
                subTask.setParentTask(task);
                subTask.save();
                taskMap.put(subTask.getId(), subTask);

                if (rand.nextBoolean())
                {
                    Task subSubTask = new Task(subTask.getName() + ".1");
                    subSubTask.setParentTask(subTask);
                    subSubTask.save();
                    taskMap.put(subSubTask.getId(), subSubTask);
                }
            }
        }

        for (int day = 30; day > -1; day--)
        {
            int seconds = 8 * 60 * 60;

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -day);
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Calendar breakCal = Calendar.getInstance();/**/
            breakCal.setTimeInMillis(cal.getTimeInMillis());
            breakCal.set(Calendar.HOUR_OF_DAY, 12);
            Date startBreak = new Date(breakCal.getTimeInMillis());
            breakCal.add(Calendar.MINUTE, 30);
            Date endBreak = new Date(breakCal.getTimeInMillis());

            Date startTime = new Date(cal.getTimeInMillis());
            Registration breakReg = null;
            int min = 60*15;
            while (seconds > 0)
            {
                int regTime = seconds > min ? rand.nextInt(seconds - min + 1) + min : seconds;
                seconds -= regTime;
                cal.add(Calendar.SECOND, regTime);

                Task task = taskMap.valueAt(rand.nextInt(taskMap.size()));
                Registration reg = new Registration(startTime, new Date(cal.getTimeInMillis()), task);
                reg.save();

                startTime = new Date(cal.getTimeInMillis());

                if (reg.getStartTime().before(startBreak) && reg.getEndTime().after(endBreak))
                {
                    breakReg = reg;
                }
            }

            if (null != breakReg && rand.nextInt(10) > 2) {
                Break b = new Break(startBreak, endBreak, breakReg, rand.nextBoolean());
                b.save();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
