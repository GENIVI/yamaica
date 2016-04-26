/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.common.core.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TimeBench
{
    protected static Map<Integer, List<Timestamp>> timestamps;

    public static void addTimestamp(int id)
    {
        addTimestamp(id, null);
    }

    public static void addTimestamp(int id, String description)
    {
        if (null == timestamps)
        {
            timestamps = new LinkedHashMap<Integer, List<Timestamp>>();
        }

        List<Timestamp> timestampList;

        if (!timestamps.containsKey(id))
        {
            timestampList = new LinkedList<Timestamp>();

            timestamps.put(id, timestampList);
        }
        else
        {
            timestampList = timestamps.get(id);
        }

        timestampList.add(new Timestamp(description));
    }

    public static void clearTimeStamps(int id)
    {
        if (null != timestamps)
        {
            timestamps.remove(id);
        }
    }

    public static void clearAllTimeStamps()
    {
        if (null != timestamps)
        {
            timestamps.clear();
        }
    }

    public static void printTimestamps(int id)
    {
        printTimestamps(id, 0);
    }

    public static void printTimestamps(int id, long minTime)
    {
        if (null != timestamps && timestamps.containsKey(id))
        {
            List<Timestamp> timestampList = timestamps.get(id);

            if (timestampList.size() > 0)
            {
                Timestamp firstTimestamp = timestampList.get(0);
                Timestamp previousTimestamp = timestampList.get(0);
                boolean skippedTimestamp = false;

                System.out.printf("%n####################### Timestamps of ID %-3d #######################%n", id);
                System.out.printf("|%-40s|%,12d|%,12d|%n", "Description", "Duration", "Absolute");

                for (Timestamp currentTimestamp : timestampList)
                {
                    long currentTime = currentTimestamp.getTimestamp();
                    long durationTime = currentTime - previousTimestamp.getTimestamp();
                    String description = currentTimestamp.getDescription();

                    if (durationTime >= minTime && null != description)
                    {
                        long absoluteTime = currentTime - firstTimestamp.getTimestamp();

                        System.out.printf(" %-40s %,12d %,12d%n", description, durationTime, absoluteTime);

                        skippedTimestamp = false;
                    }
                    else if (durationTime > 0 && false == skippedTimestamp)
                    {
                        System.out.printf(" ...%n");

                        skippedTimestamp = true;
                    }

                    previousTimestamp = currentTimestamp;
                }
            }
        }
    }

    public static void printAllTimestamps()
    {
        printAllTimestamps(0);
    }

    public static void printAllTimestamps(long minTime)
    {
        if (null != timestamps)
        {
            for (int id : timestamps.keySet())
            {
                printTimestamps(id, minTime);
            }
        }
    }

    protected static class Timestamp
    {
        private final String description;
        private final long   timestamp;

        public Timestamp(String description)
        {
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }

        public String getDescription()
        {
            return description;
        }

        public long getTimestamp()
        {
            return timestamp;
        }
    }

    /*****************************************************************************/

    protected static Map<Integer, Timer> timers;

    public static void startTimer(int id, String description)
    {
        if (null == timers)
        {
            timers = new HashMap<Integer, Timer>();
        }

        Timer timer;

        if (!timers.containsKey(id))
        {
            timer = new Timer(description);

            timers.put(id, timer);
        }
        else
        {
            timer = timers.get(id);
        }

        timer.start();
    }

    public static void stopTimer(int id)
    {
        if (null != timers && timers.containsKey(id))
        {
            timers.get(id).stop();
        }
    }

    public static void clearTimer(int id)
    {
        if (null != timers)
        {
            timers.remove(id);
        }
    }

    public static void clearAllTimers()
    {
        if (null != timers)
        {
            timers.clear();
        }
    }

    public static void printAllTimers()
    {
        System.out.printf("%n##################################### Details of Timers #####################################%n");
        System.out.printf("|%5s|%-40s|%12s|%8s|%8s:%6s:%6s|%n", "ID", "Description", "Time", "Runs", "Time/Run", "Min", "Max");

        if (null != timers)
        {
            for (int id : timers.keySet())
            {
                Timer timer = timers.get(id);
                long totalTime = timer.getTotalTime();
                int runs = timer.getRuns();
                double timePerRun = (runs > 0) ? (double) totalTime / (double) runs : 0;

                System.out.printf(" %5d %-40s %,12d %,8d %,8.2f %,6d %,6d%n", id, timer.getDescription(), totalTime, runs, timePerRun,
                        timer.getMinTime(), timer.getMaxTime());
            }
        }
    }

    protected static class Timer
    {
        private static final int STOPPED       = 0;
        private static final int STARTED       = 1;

        private final String     description;
        private long             totalTime     = 0;
        private long             minTime       = Long.MAX_VALUE;
        private long             maxTime       = 0;
        private long             lastStartTime = 0;
        private int              runs          = 0;
        private int              state         = STOPPED;

        public Timer(String description)
        {
            this.description = description;
        }

        public void start()
        {
            if (STOPPED == state)
            {
                state = STARTED;
                lastStartTime = System.currentTimeMillis();
            }
        }

        public void stop()
        {
            if (STARTED == state)
            {
                long runTime = System.currentTimeMillis() - lastStartTime;
                totalTime += runTime;
                minTime = Math.min(minTime, runTime);
                maxTime = Math.max(maxTime, runTime);
                runs++;
                state = STOPPED;
            }
        }

        public String getDescription()
        {
            return description;
        }

        public long getTotalTime()
        {
            return totalTime;
        }

        public long getMinTime()
        {
            return (runs > 0) ? minTime : 0;
        }

        public long getMaxTime()
        {
            return maxTime;
        }

        public int getRuns()
        {
            return runs;
        }
    }
}
