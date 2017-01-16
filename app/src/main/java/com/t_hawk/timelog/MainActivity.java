package com.t_hawk.timelog;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.orm.SugarDb;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.t_hawk.timelog.fragments.TaskListFragment;
import com.t_hawk.timelog.model.Break;
import com.t_hawk.timelog.model.Registration;
import com.t_hawk.timelog.model.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements TaskListFragment.OnListFragmentInteractionListener {

    private static final int FirstDayOfWeek = Calendar.SUNDAY;
    private static final int LastDayOfWeek = Calendar.SATURDAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BuildDrawer(toolbar);

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

        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_WEEK);

        TaskFilter filter = (FirstDayOfWeek == day && !HasRegistrations(today)) ? TaskFilter.lastWeek : TaskFilter.thisWeek;
        LoadFilterFragment(filter);
    }

    private boolean HasRegistrations(Calendar today) {
        // TODO: Check in Sugar
        return false;
    }

    private ArrayList<Task> getTasks(Calendar from, Calendar to) {
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

        return tasks;
    }

    private void BuildDrawer(Toolbar toolbar) {
        long id = 1;
        PrimaryDrawerItem taskItem = new PrimaryDrawerItem().withIdentifier(id++).withName(R.string.drawer_item_tasks).withIcon(GoogleMaterial.Icon.gmd_home);

        final LongSparseArray<TaskFilter> filterMap = new LongSparseArray<>();
        List<IDrawerItem> drawerItems = new LinkedList<>();

        filterMap.put(++id, TaskFilter.yesterday);
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(id).withName(R.string.drawer_item_yesterday).withIcon(GoogleMaterial.Icon.gmd_date_range));
        filterMap.put(++id, TaskFilter.today);
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(id).withName(R.string.drawer_item_today).withIcon(GoogleMaterial.Icon.gmd_date_range));
        filterMap.put(++id, TaskFilter.lastWeek);
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(id).withName(R.string.drawer_item_lastWeek).withIcon(GoogleMaterial.Icon.gmd_date_range));
        filterMap.put(++id, TaskFilter.thisWeek);
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(id).withName(R.string.drawer_item_thisWeek).withIcon(GoogleMaterial.Icon.gmd_date_range));
        filterMap.put(++id, TaskFilter.lastMonth);
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(id).withName(R.string.drawer_item_lastMonth).withIcon(GoogleMaterial.Icon.gmd_date_range));
        filterMap.put(++id, TaskFilter.thisMonth);
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(id).withName(R.string.drawer_item_thisMonth).withIcon(GoogleMaterial.Icon.gmd_date_range));
        filterMap.put(++id, TaskFilter.year);
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(id).withName(R.string.drawer_item_year).withIcon(GoogleMaterial.Icon.gmd_date_range));
        filterMap.put(++id, TaskFilter.period);
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(id).withName(R.string.drawer_item_period).withIcon(GoogleMaterial.Icon.gmd_date_range));

        new DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .addDrawerItems(
                    taskItem
                    , new DividerDrawerItem()
            )
            .addDrawerItems(drawerItems.toArray(new IDrawerItem[0]))
            .addDrawerItems(
                new DividerDrawerItem()
                , new SecondaryDrawerItem().withIdentifier(++id).withName(R.string.drawer_item_settings).withIcon(GoogleMaterial.Icon.gmd_settings)
            )
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    TaskFilter filter = filterMap.get(drawerItem.getIdentifier());
                    if (null == filter) filter = TaskFilter.none;

                    LoadFilterFragment(filter);

                    return false;
                }
            })
            .build();
    }

    private void LoadFilterFragment(TaskFilter filter) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // TODO: First-day-of-week should be a configuration setting
        int firstDayOfWeek = FirstDayOfWeek;
        // TODO: Last-day-of-week should be a configuration setting
        int lastDayOfWeek = LastDayOfWeek;

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        switch (filter)
        {
            case yesterday:
                from.add(Calendar.DATE, -1);
                to.add(Calendar.DATE, -1);
                break;
            case today:
                break;
            case lastWeek:
                from.add(Calendar.WEEK_OF_YEAR, -1);
                to.add(Calendar.WEEK_OF_YEAR, -1);
            case thisWeek:
                from.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
                to.set(Calendar.DAY_OF_WEEK, lastDayOfWeek);
                break;
            case lastMonth:
                from.add(Calendar.MONTH, -1);
                to.add(Calendar.MONTH, -1);
            case thisMonth:
                from.set(Calendar.DATE, from.getActualMinimum(Calendar.DATE));
                to.set(Calendar.DATE, to.getActualMaximum(Calendar.DATE));
                break;
            case year:
                from.set(Calendar.DAY_OF_YEAR, from.getActualMinimum(Calendar.DAY_OF_YEAR));
                to.set(Calendar.DAY_OF_YEAR, to.getActualMaximum(Calendar.DAY_OF_YEAR));
                break;
            case period:
                // TODO: Implement this using a dialog
                break;
            default:
                // TODO: Handle not a period filter
                break;
        }

        TaskListFragment taskListFragment = TaskListFragment.newInstance(getTasks(from, to));
        transaction.add(R.id.main_fragment_container, taskListFragment);
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(Task item) {
        // TODO : Implement this
        return;
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
