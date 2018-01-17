/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import org.master.unitoo.core.types.Month;
import org.master.unitoo.core.types.WeekDay;

/**
 *
 * @author Andrey
 */
public abstract class BaseDateTask extends BaseBackgroundTask {

    protected abstract Month[] months();

    protected abstract WeekDay[] weekdays();

    protected abstract String days();

    protected abstract String times();

    @Override
    protected long calcNext() {
        HashSet<Integer> iMonths = new HashSet<>();
        HashSet<Integer> iDays = new HashSet<>();
        HashSet<Integer> iWDays = new HashSet<>();

        Month[] tMonths = months();
        if (tMonths == null || tMonths.length == 0) {
            tMonths = Month.values();
        }

        for (Month month : tMonths) {
            iMonths.add(month.calendar());
        }

        WeekDay[] tWDays = weekdays();
        if (tWDays == null || tWDays.length == 0) {
            tWDays = WeekDay.values();
        }

        for (WeekDay wd : tWDays) {
            iWDays.add(wd.calendar());
        }

        String days = days();
        if (days == null || days.trim().isEmpty()) {
            for (int i = 1; i < 32; i++) {
                iDays.add(i);
            }
        } else {
            String[] tDays = days.split(";");
            for (String day : tDays) {
                try {
                    iDays.add(Integer.valueOf(day.trim()));
                } catch (Throwable t) {
                    log().warning("Can't parse day from settings for '" + name() + "': " + day);
                }
            }
        }

        String times = times();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int hops = 0;
        long exectime = 0;
        while (hops < 1000) {
            if (iMonths.contains(calendar.get(Calendar.MONTH))
                    && iWDays.contains(calendar.get(Calendar.DAY_OF_WEEK))
                    && iDays.contains(calendar.get(Calendar.DAY_OF_MONTH))) {

                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                ArrayList<Long> timeList = new ArrayList<>();
                if (times != null && !times.trim().isEmpty()) {
                    String[] tTimes = times.split(";");
                    for (String time : tTimes) {
                        try {
                            String[] timeParts = time.split(":");
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeParts[0].trim()));
                            calendar.set(Calendar.MINUTE, Integer.valueOf(timeParts[1].trim()));
                            timeList.add(calendar.getTimeInMillis());
                        } catch (Throwable t) {
                            log().warning("Can't parse time from settings for '" + name() + "': " + time);
                        }
                    }
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    timeList.add(calendar.getTimeInMillis());
                }

                Collections.sort(timeList);
                for (Long item : timeList) {
                    if (System.currentTimeMillis() < item) {
                        exectime = item;
                        break;
                    }
                }

                if (exectime != 0) {
                    break;
                }
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            hops++;
        }

        if (exectime == 0) {
            return 0;
        } else {
            exectime = exectime - System.currentTimeMillis();
            return exectime < 0 ? 1 : exectime;
        }
    }

}
