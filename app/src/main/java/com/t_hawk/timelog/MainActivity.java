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
import com.t_hawk.timelog.fragments.TaskListFragment;
import com.t_hawk.timelog.helpers.TaskHelper;
import com.t_hawk.timelog.model.Task;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements TaskListFragment.OnListFragmentInteractionListener,
        com.borax12.materialdaterangepicker.date.DatePickerDialog.OnDateSetListener
{
    // TODO: First-day-of-week should be a configuration setting
    private static final int FirstDayOfWeek = Calendar.SUNDAY;

    // TODO: Last-day-of-week should be a configuration setting
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

        TaskHelper.buildDummyData(getApplicationContext());

        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_WEEK);

        Calendar from = Calendar.getInstance();
        TaskHelper.setTimeOfDay(from, 0, 0, 0, 0);

        Calendar to = Calendar.getInstance();
        TaskHelper.setTimeOfDay(to, 23, 59, 59, 999);

        TaskFilter filter;
        if (TaskHelper.hasRegistrations(from, to)) {
            filter = TaskFilter.today;
        } else if (FirstDayOfWeek == day) {
            filter = TaskFilter.lastWeek;
        } else {
            filter = TaskFilter.thisWeek;
        }

        LoadFilterFragment(filter);
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
        Calendar from = Calendar.getInstance();
        TaskHelper.setTimeOfDay(from, 0, 0, 0, 0);

        Calendar to = Calendar.getInstance();
        TaskHelper.setTimeOfDay(to, 23, 59, 59, 999);
        int titleId = -1;

        switch (filter) {
            case yesterday:
                from.add(Calendar.DATE, -1);
                to.add(Calendar.DATE, -1);
                titleId = R.string.drawer_item_yesterday;
                break;
            case today:
                titleId = R.string.drawer_item_today;
                break;
            case lastWeek:
                from.add(Calendar.WEEK_OF_YEAR, -1);
                to.add(Calendar.WEEK_OF_YEAR, -1);
                titleId = R.string.drawer_item_lastWeek;
            case thisWeek:
                from.set(Calendar.DAY_OF_WEEK, FirstDayOfWeek);
                to.set(Calendar.DAY_OF_WEEK, LastDayOfWeek);
                if (-1 == titleId) titleId = R.string.drawer_item_thisWeek;
                break;
            case lastMonth:
                from.add(Calendar.MONTH, -1);
                to.add(Calendar.MONTH, -1);
                titleId = R.string.drawer_item_lastMonth;
            case thisMonth:
                from.set(Calendar.DATE, from.getActualMinimum(Calendar.DATE));
                to.set(Calendar.DATE, to.getActualMaximum(Calendar.DATE));
                if (-1 == titleId) titleId = R.string.drawer_item_thisMonth;
                break;
            case year:
                from.set(Calendar.DAY_OF_YEAR, from.getActualMinimum(Calendar.DAY_OF_YEAR));
                to.set(Calendar.DAY_OF_YEAR, to.getActualMaximum(Calendar.DAY_OF_YEAR));
                titleId = R.string.drawer_item_year;
                break;
            case period:
                from.set(Calendar.DAY_OF_WEEK, FirstDayOfWeek);
                to.set(Calendar.DAY_OF_WEEK, LastDayOfWeek);

                com.borax12.materialdaterangepicker.date.DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(this, from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH), to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH));
                dpd.show(getFragmentManager(), "DatePickerDialog");

                // We do not wish to update the list immediately
                return;
            default:
                // TODO: Handle not a period filter
                titleId = R.string.app_name;
                break;
        }

        LoadTaskListFragment(titleId, from, to);
    }

    private void LoadTaskListFragment(int titleId, Calendar from, Calendar to) {
        setTitle(titleId);

        TaskListFragment taskListFragment = TaskListFragment.newInstance(TaskHelper.getTasks(from, to));

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, taskListFragment);
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(Task item) {
        // TODO : Implement this
        return;
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

    @Override
    public void onDateSet(com.borax12.materialdaterangepicker.date.DatePickerDialog datePicker, int year, int month, int day, int yearEnd, int monthEnd, int dayEnd) {
        Calendar periodFrom = Calendar.getInstance();
        periodFrom.set(year, month, day);

        Calendar periodTo = Calendar.getInstance();
        periodTo.set(yearEnd, monthEnd, dayEnd);

        LoadTaskListFragment(R.string.drawer_item_period, periodFrom, periodTo);
    }
}
