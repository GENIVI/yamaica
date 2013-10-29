package de.bmw.yamaica.base.core.utils;

import java.util.HashMap;
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
            timestamps = new HashMap<Integer, List<Timestamp>>();
        }

        List<Timestamp> timestampList;

        if (!timestamps.containsKey(id))
        {
            timestampList = new LinkedList<TimeBench.Timestamp>();

            timestamps.put(id, timestampList);
        }
        else
        {
            timestampList = timestamps.get(id);
        }

        timestampList.add(new Timestamp(description));
    }

    public static void resetTimer(int id)
    {
        if (null != timestamps)
        {
            timestamps.remove(id);
        }
    }

    public static void resetAllTimers()
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

                System.out.printf("%n######################## Timestamps of ID %-3d ########################%n", id);
                System.out.printf("  |Description                             |    Duration|    Absolute|%n");

                for (Timestamp currentTimestamp : timestampList)
                {
                    long currentTime = currentTimestamp.getTimestamp();
                    long durationTime = currentTime - previousTimestamp.getTimestamp();
                    String description = currentTimestamp.getDescription();

                    if (durationTime >= minTime && null != description)
                    {
                        long absoluteTime = currentTime - firstTimestamp.getTimestamp();

                        System.out.printf(" - %-40s %,12d %,12d%n", description, durationTime, absoluteTime);
                    }
                    else if (durationTime > 0)
                    {
                        System.out.printf(" - ...%n");
                    }

                    previousTimestamp = currentTimestamp;
                }
            }
        }
    }

    protected static class Timestamp
    {
        protected final String description;
        protected final long   timestamp;

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
}
