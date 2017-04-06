package com.t_hawk.timelog.helpers;

import android.content.Context;
import android.util.LongSparseArray;

import com.orm.SugarDb;
import com.orm.query.Condition;
import com.orm.query.Select;
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

/**
 * Project: TimeLog
 * Created by Morten on 4/6/2017.
 *
 * @author Morten
 */

public class TaskHelper {
    public static void setTimeOfDay(Calendar calendar, int hours, int minutes, int seconds, int milliseconds) {
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, milliseconds);
    }

    public static void buildDummyData(Context context) {
        SugarDb sugarDb = new SugarDb(context);
        new File(sugarDb.getDB().getPath()).delete();
        Task.findById(Task.class, 1);
        Registration.findById(Registration.class, 1);
        Break.findById(Break.class, 1);

        Random rand = new Random();

        LongSparseArray<Task> taskMap = new LongSparseArray<>();
        for (int i = 0; i < 10; i++) {
            Task task = new Task("Task " + (i + 1));
            task.save();
            taskMap.put(task.getId(), task);

            int sub = rand.nextInt(5);
            for (int j = 0; j < sub; j++) {
                Task subTask = new Task(task.getName() + "." + (j + 1));
                subTask.setParentTask(task);
                subTask.save();
                taskMap.put(subTask.getId(), subTask);

                if (rand.nextBoolean()) {
                    Task subSubTask = new Task(subTask.getName() + ".1");
                    subSubTask.setParentTask(subTask);
                    subSubTask.save();
                    taskMap.put(subSubTask.getId(), subSubTask);
                }
            }
        }

        for (int day = 30; day > -1; day--) {
            int seconds = 8 * 60 * 60;

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -day);
            setTimeOfDay(cal, 8, 0, 0, 0);

            Calendar breakCal = Calendar.getInstance();/**/
            breakCal.setTimeInMillis(cal.getTimeInMillis());
            breakCal.set(Calendar.HOUR_OF_DAY, 12);
            Date startBreak = new Date(breakCal.getTimeInMillis());
            breakCal.add(Calendar.MINUTE, 30);
            Date endBreak = new Date(breakCal.getTimeInMillis());

            Date startTime = new Date(cal.getTimeInMillis());
            Registration breakReg = null;
            int min = 60 * 15;
            while (seconds > 0) {
                int regTime = seconds > min ? rand.nextInt(seconds - min + 1) + min : seconds;
                seconds -= regTime;
                cal.add(Calendar.SECOND, regTime);

                Task task = taskMap.valueAt(rand.nextInt(taskMap.size()));
                Registration reg = new Registration(startTime, new Date(cal.getTimeInMillis()), task);
                reg.save();

                startTime = new Date(cal.getTimeInMillis());

                if (reg.getStartTime().before(startBreak) && reg.getEndTime().after(endBreak)) {
                    breakReg = reg;
                }
            }

            if (null != breakReg && rand.nextInt(10) > 2) {
                Break b = new Break(startBreak, endBreak, breakReg, rand.nextBoolean());
                b.save();
            }
        }
    }

    public static boolean hasRegistrations(Calendar from, Calendar to) {
        return Select.from(Registration.class).and(
                Condition.prop("start_time").gt(from.getTimeInMillis()),
                Condition.prop("start_time").lt(to.getTimeInMillis())).count() > 0;
    }

    public static ArrayList<Task> getTasks(Calendar from, Calendar to) {
        List<Registration> registrations = Select.from(Registration.class).and(
                Condition.prop("start_time").gt(from.getTimeInMillis()),
                Condition.prop("end_time").lt(to.getTimeInMillis())
        ).list();

        HashSet<Long> seen = new HashSet<>();
        ArrayList<Task> tasks = new ArrayList<>();
        for (Registration r : registrations) {
            Task t = r.getTask();
            if (t == null) continue;
            while (t.getParentTask() != null) {
                t = t.getParentTask();
            }

            if (!seen.contains(t.getId())) {
                seen.add(t.getId());
                tasks.add(t);
            }
        }

        return tasks;
    }
}
